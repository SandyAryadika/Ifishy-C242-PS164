const pool = require('../config/dbConfig');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');
const { Storage } = require('@google-cloud/storage');
const path = require('path');
const { JWT_SECRET } = require('../config/secrets');

// Register User
async function registerUser(req, res) {
  const { username, email, password, confirmPassword } = req.body;

  // Validasi input: pastikan username dan email tidak kosong
  if (!username || !email || !password || !confirmPassword) {
    return res.status(400).json({
      success: false,
      message: 'Username, email, password, and confirm password are required.',
    });
  }

  // Validasi password dan confirmPassword
  if (password !== confirmPassword) {
    return res.status(400).json({
      success: false,
      message: 'Passwords do not match',
    });
  }

  try {
    // Hash password
    const hashedPassword = await bcrypt.hash(password, 10);

    // Simpan user ke database
    const query = 'INSERT INTO users (username, email, password) VALUES (?, ?, ?)';
    const [result] = await pool.query(query, [username, email, hashedPassword]);

    res.status(201).json({
      success: true,
      message: 'User successfully registered!',
      user: {
        id: result.insertId,
        username,
        email,
      },
    });
  } catch (error) {
    console.error('Error in registerUser:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to create user',
    });
  }
}

// Login User
async function loginUser(req, res) {
  const { email, password } = req.body;

  try {
    // Cari user berdasarkan email
    const query = 'SELECT * FROM users WHERE email = ?';
    const [rows] = await pool.query(query, [email]);

    if (rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'User not found',
      });
    }

    const user = rows[0];

    // Cek password
    const isPasswordValid = await bcrypt.compare(password, user.password);
    if (!isPasswordValid) {
      return res.status(401).json({
        success: false,
        message: 'Invalid credentials',
      });
    }

    // Buat token JWT
    const jwtSecret = process.env.JWT_SECRET;

    if (!jwtSecret) {
      console.error("JWT_SECRET is not defined in environment variables");
      throw new Error("JWT_SECRET is missing");
    }

    const token = jwt.sign({ id: user.id, email: user.email }, jwtSecret, { expiresIn: '1h' });


    res.status(200).json({
      success: true,
      message: 'Login successful',
      token,
    });
  } catch (error) {
    console.error('Error in loginUser:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to login',
    });
  }
}

const getAllUsers = async (req, res) => {
  try {
      const [rows] = await pool.query("SELECT id, username, email FROM users");
      res.status(200).json({
          success: true,
          users: rows,
      });
  } catch (error) {
      console.error("Error fetching users:", error);
      res.status(500).json({
          success: false,
          message: "Failed to fetch users",
      });
  }
};

// Fungsi untuk mengupdate profil pengguna
const updateUserProfile = async (req, res) => {
  const { username, email, password, newPassword } = req.body;
  const token = req.headers.authorization?.split(" ")[1];

  if (!token) {
      return res.status(401).json({
          success: false,
          message: "Token is required",
      });
  }

  try {
      // Verifikasi token dan dapatkan ID user
      const decoded = jwt.verify(token, process.env.JWT_SECRET);
      const userId = decoded.id;

      // Ambil data user dari database
      const [userRows] = await pool.query("SELECT * FROM users WHERE id = ?", [userId]);
      if (userRows.length === 0) {
          return res.status(404).json({
              success: false,
              message: "User not found",
          });
      }

      // Update username dan email jika disertakan dalam request
      const updatedUsername = username || userRows[0].username;
      const updatedEmail = email || userRows[0].email;

      // Jika password baru diberikan, perbarui password
      let updatedPassword = userRows[0].password;
      if (newPassword) {
          // Periksa password lama sebelum mengganti dengan yang baru
          const isPasswordValid = await bcrypt.compare(password, userRows[0].password);
          if (!isPasswordValid) {
              return res.status(400).json({
                  success: false,
                  message: "Incorrect password",
              });
          }

          // Enkripsi password baru
          updatedPassword = await bcrypt.hash(newPassword, 10);
      }

      // Update ke database
      await pool.query(
          "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?",
          [updatedUsername, updatedEmail, updatedPassword, userId]
      );

      res.status(200).json({
          success: true,
          message: "User profile updated successfully",
      });
  } catch (error) {
      console.error("Error in updateUserProfile:", error);
      res.status(500).json({
          success: false,
          message: "Failed to update user profile",
      });
  }
};

// Fungsi untuk menghapus akun pengguna
const deleteUserAccount = async (req, res) => {
  const token = req.headers.authorization?.split(" ")[1];

  if (!token) {
      return res.status(401).json({
          success: false,
          message: "Token is required",
      });
  }

  try {
      // Verifikasi token dan dapatkan ID user
      const decoded = jwt.verify(token, process.env.JWT_SECRET);
      const userId = decoded.id;

      // Hapus data user dari database
      const result = await pool.query("DELETE FROM users WHERE id = ?", [userId]);
      if (result.affectedRows === 0) {
          return res.status(404).json({
              success: false,
              message: "User not found",
          });
      }

      res.status(200).json({
          success: true,
          message: "User account deleted successfully",
      });
  } catch (error) {
      console.error("Error in deleteUserAccount:", error);
      res.status(500).json({
          success: false,
          message: "Failed to delete user account",
      });
  }
};

// Logout User
const logoutUser = async (req, res) => {
  try {
    // Hapus token dari sisi klien (opsional untuk server)
    res.status(200).json({
      success: true,
      message: 'Logout successful',
    });
  } catch (error) {
    console.error("Error in logoutUser:", error);
    res.status(500).json({
      success: false,
      message: "Failed to logout",
    });
  }
};

const storage = new Storage({
  projectId: 'bangkit-capstone-ps164',
  keyFilename: './service-account-key.json',
});

// Nama bucket
const bucketName = 'ifishy-photos';
const profileFolder = 'photo-profile-user';
const postFolder = 'image-post';

const uploadProfilePhoto = async (req, res) => {
  try {
    const { userId } = req.body; // Ambil userId dari body
    const file = req.file; // File yang diunggah

    if (!file) {
      return res.status(400).json({ success: false, message: 'No file uploaded' });
    }

    // Upload ke bucket GCS di folder photo-profile-user
    const blob = storage.bucket(bucketName).file(`${profileFolder}/${Date.now()}-${file.originalname}`);
    const blobStream = blob.createWriteStream({
      resumable: false,
      contentType: file.mimetype,
    });

    blobStream.on('error', (err) => {
      console.error('Error uploading to GCS:', err);
      res.status(500).json({ success: false, message: 'Failed to upload profile photo' });
    });

    blobStream.on('finish', async () => {
      const publicUrl = `https://storage.googleapis.com/${bucketName}/${blob.name}`;

      // Update URL foto profil di database
      const sql = 'UPDATE users SET profile_photo = ? WHERE id = ?';
      await pool.query(sql, [publicUrl, userId]);

      res.status(200).json({
        success: true,
        message: 'Profile photo uploaded successfully!',
        photoUrl: publicUrl,
      });
    });

    blobStream.end(file.buffer);
  } catch (error) {
    console.error('Error in uploadProfilePhoto:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to upload profile photo',
    });
  }
};

// Fungsi untuk mendapatkan data dashboard berdasarkan email
const getDashboardData = async (req, res) => {
  const { email } = req.params;

  try {
    // Query untuk mendapatkan data pengguna berdasarkan email
    const [rows] = await pool.query('SELECT id, username, email, profile_photo FROM users WHERE email = ?', [email]);

    if (rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'User not found',
      });
    }

    // Mengembalikan data pengguna
    res.status(200).json({
      success: true,
      message: 'Dashboard data retrieved successfully',
      data: rows[0], // Mengambil data pengguna pertama yang ditemukan
    });
  } catch (error) {
    console.error('Error in getDashboardData:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to retrieve dashboard data',
    });
  }
};

// Mendapatkan profile user berdasarkan email
const getUserProfile = async (req, res) => {
  try {
    const { email } = req.params; // Mengambil email dari parameter URL

    // Query untuk mendapatkan data user berdasarkan email
    const sql = 'SELECT id, username, email, profile_photo FROM users WHERE email = ?';
    const [rows] = await pool.query(sql, [email]); 

    if (rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'User not found',
      });
    }

    res.status(200).json({
      success: true,
      message: 'User profile retrieved successfully',
      profile: rows[0],
    });
  } catch (error) {
    console.error('Error in getUserProfile:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to retrieve user profile',
    });
  }
};

// membuat postingan
const createPost = async (req, res) => {
  try {
    const { title, content } = req.body;
    const { user } = req; // Informasi user dari token JWT
    const file = req.file; // File yang diunggah

    let imageUrl = null;
    if (file) {
      // Upload ke bucket GCS di folder image-post
      const blob = storage.bucket(bucketName).file(`${postFolder}/${Date.now()}-${file.originalname}`);
      const blobStream = blob.createWriteStream({
        resumable: false,
        contentType: file.mimetype,
      });

      await new Promise((resolve, reject) => {
        blobStream.on('error', reject);
        blobStream.on('finish', resolve);
        blobStream.end(file.buffer);
      });

      imageUrl = `https://storage.googleapis.com/${bucketName}/${blob.name}`;
    }

    // Simpan data postingan ke database
    const sql = 'INSERT INTO posts (user_id, title, content, image_url) VALUES (?, ?, ?, ?)';
    const [result] = await pool.query(sql, [user.id, title, content, imageUrl]);

    res.status(201).json({
      success: true,
      message: 'Post created successfully!',
      postId: result.insertId,
    });
  } catch (error) {
    console.error('Error in createPost:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to create post',
    });
  }
};

const getPosts = async (req, res) => {
  try {
    // Ambil semua postingan beserta username dari user yang mem-posting
    const sqlPosts = `
      SELECT p.id, p.title, p.content, p.image_url, p.created_at, p.user_id, u.username
      FROM posts p
      JOIN users u ON p.user_id = u.id
      ORDER BY p.created_at DESC
    `;
    const [posts] = await pool.query(sqlPosts);

    // Ambil komentar, jumlah likes, share, dan votes untuk setiap postingan
    const postsWithDetails = await Promise.all(posts.map(async (post) => {
      // Ambil komentar berdasarkan post_id
      const sqlComments = `
        SELECT c.id, c.content, c.created_at, u.username
        FROM comments c
        JOIN users u ON c.user_id = u.id
        WHERE c.post_id = ?
        ORDER BY c.created_at ASC
      `;
      const [comments] = await pool.query(sqlComments, [post.id]);

      // Hitung jumlah likes untuk setiap postingan
      const sqlLikes = `
        SELECT COUNT(*) AS like_count
        FROM post_votes
        WHERE post_id = ? AND vote_type = 'upvote'
      `;
      const [likeResult] = await pool.query(sqlLikes, [post.id]);
      const likeCount = likeResult[0].like_count || 0;

      // Hitung jumlah share untuk setiap postingan
      const sqlShares = `
        SELECT COUNT(*) AS share_count
        FROM post_votes
        WHERE post_id = ? AND vote_type = 'share'
      `;
      const [shareResult] = await pool.query(sqlShares, [post.id]);
      const shareCount = shareResult[0].share_count || 0;

      // Hitung jumlah votes untuk setiap postingan (upvote + downvote)
      const sqlVotes = `
        SELECT COUNT(*) AS vote_count
        FROM post_votes
        WHERE post_id = ?
      `;
      const [voteResult] = await pool.query(sqlVotes, [post.id]);
      const voteCount = voteResult[0].vote_count || 0; // pastikan voteCount didefinisikan di sini

      // Hitung waktu sejak komentar
      const formattedComments = comments.map(comment => ({
        ...comment,
        timeSinceCommented: `${Math.floor((Date.now() - new Date(comment.created_at)) / 60000)} minutes ago`
      }));

      return {
        ...post,
        comments: formattedComments, // Menambahkan komentar ke setiap postingan
        likeCount,                   // Menambahkan jumlah likes
        shareCount,                  // Menambahkan jumlah share
        voteCount,                   // Menambahkan jumlah votes
        timeSincePosted: `${Math.floor((Date.now() - new Date(post.created_at)) / 60000)} minutes ago`,
      };
    }));

    res.status(200).json({
      success: true,
      posts: postsWithDetails, // Mengirimkan postingan beserta komentar, likes, shares, dan votes
    });
  } catch (error) {
    console.error('Error in getPosts:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to retrieve posts',
    });
  }
};

const getPostById = async (req, res) => {
  const { id } = req.params;

  try {
    // Ambil postingan berdasarkan ID
    const sqlPost = `
      SELECT p.id, p.title, p.content, p.image_url, p.created_at, p.user_id, u.username
      FROM posts p
      JOIN users u ON p.user_id = u.id
      WHERE p.id = ?
    `;
    const [postResult] = await pool.query(sqlPost, [id]);

    if (postResult.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Post not found',
      });
    }

    const post = postResult[0];

    // Ambil komentar untuk postingan tersebut
    const sqlComments = `
      SELECT c.id, c.content, c.created_at, u.username
      FROM comments c
      JOIN users u ON c.user_id = u.id
      WHERE c.post_id = ?
      ORDER BY c.created_at ASC
    `;
    const [comments] = await pool.query(sqlComments, [id]);

    // Hitung jumlah likes untuk postingan tersebut
    const sqlLikes = `
      SELECT COUNT(*) AS like_count
      FROM post_votes
      WHERE post_id = ? AND vote_type = 'upvote'
    `;
    const [likeResult] = await pool.query(sqlLikes, [id]);
    const likeCount = likeResult[0].like_count || 0;

    // Hitung jumlah share untuk postingan tersebut
    const sqlShares = `
      SELECT COUNT(*) AS share_count
      FROM post_votes
      WHERE post_id = ?
    `;
    const [shareResult] = await pool.query(sqlShares, [id]);
    const shareCount = shareResult[0].share_count || 0;

    // Format komentar dan hitung waktu sejak komentar
    const formattedComments = comments.map(comment => ({
      ...comment,
      timeSinceCommented: `${Math.floor((Date.now() - new Date(comment.created_at)) / 60000)} minutes ago`
    }));

    res.status(200).json({
      success: true,
      post: {
        ...post,
        comments: formattedComments,
        likeCount,     // Menambahkan jumlah likes
        shareCount,    // Menambahkan jumlah share
        timeSincePosted: `${Math.floor((Date.now() - new Date(post.created_at)) / 60000)} minutes ago`,
      },
    });
  } catch (error) {
    console.error('Error in getPostById:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to retrieve the post',
    });
  }
};

const addShareToPost = async (req, res) => {
  try {
    const { postId } = req.params;

    // Update jumlah share pada postingan
    const updateShareCountQuery = 'UPDATE posts SET share_count = share_count + 1 WHERE id = ?';
    await pool.query(updateShareCountQuery, [postId]);

    res.status(200).json({
      success: true,
      message: 'Share added successfully',
    });
  } catch (error) {
    console.error('Error adding share to post:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to add share to post',
    });
  }
};

//User dapat berkomentar pada postingan
const addComment = async (req, res) => {
  try {
    const { content } = req.body;
    const { user } = req;
    const { postId } = req.params;

    // Periksa jika postId valid
    if (!postId) {
      return res.status(400).json({
        success: false,
        message: 'Post ID is required',
      });
    }

    if (!content) {
      return res.status(400).json({
        success: false,
        message: 'Content is required',
      });
    }

    const sql = 'INSERT INTO comments (post_id, user_id, content) VALUES (?, ?, ?)';
    await pool.query(sql, [postId, user.id, content]);

    res.status(201).json({
      success: true,
      message: 'Comment added successfully!',
    });
  } catch (error) {
    console.error('Error in addComment:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to add comment',
    });
  }
};

//Menampilkan semua komentar pada sebuah postingan
const getComments = async (req, res) => {
  try {
    const { postId } = req.params;

    // Query untuk mendapatkan semua komentar terkait postingan
    const commentsSql = `
      SELECT c.id, c.content, c.created_at, u.username
      FROM comments c
      JOIN users u ON c.user_id = u.id
      WHERE c.post_id = ?
      ORDER BY c.created_at ASC
    `;
    const [comments] = await pool.query(commentsSql, [postId]);

    // Query untuk mendapatkan semua balasan komentar terkait postingan
    const repliesSql = `
      SELECT r.id, r.content, r.created_at, r.parent_comment_id, u.username
      FROM replies r
      JOIN users u ON r.user_id = u.id
      WHERE r.parent_comment_id IN (SELECT id FROM comments WHERE post_id = ?)
      ORDER BY r.created_at ASC
    `;
    const [replies] = await pool.query(repliesSql, [postId]);

    // Gabungkan komentar dengan balasannya dalam struktur nested
    const formattedComments = comments.map((comment) => {
      const commentReplies = replies
        .filter((reply) => reply.parent_comment_id === comment.id)
        .map((reply) => ({
          id: reply.id,
          content: reply.content,
          created_at: reply.created_at,
          username: reply.username,
          timeSinceReplied: `${Math.floor((Date.now() - new Date(reply.created_at)) / 60000)} minutes ago`,
        }));

      return {
        ...comment,
        timeSinceCommented: `${Math.floor((Date.now() - new Date(comment.created_at)) / 60000)} minutes ago`,
        replies: commentReplies, // Tambahkan array replies ke setiap komentar
      };
    });

    res.status(200).json({
      success: true,
      comments: formattedComments,
    });
  } catch (error) {
    console.error('Error in getComments:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to retrieve comments',
    });
  }
};

//Membalas komentar
const addReplyToComment = async (req, res) => {
  try {
    const { commentId } = req.params;
    const { userId, content } = req.body; // userId dan content harus dikirimkan dari client

    if (!content) {
      return res.status(400).json({
        success: false,
        message: 'Reply content is required',
      });
    }

    const sql = 'INSERT INTO replies (parent_comment_id, user_id, content) VALUES (?, ?, ?)';
    await pool.query(sql, [commentId, userId, content]);

    res.status(201).json({
      success: true,
      message: 'Reply added successfully',
    });
  } catch (error) {
    console.error('Error adding reply to comment:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to add reply to comment',
    });
  }
};

const getCommentsWithReplies = async (req, res) => {
  try {
    const { postId } = req.params;

    // Query untuk mendapatkan semua komentar terkait postingan
    const commentsSql = `
      SELECT c.id, c.content, c.created_at, u.username,
             (SELECT COUNT(*) FROM likes WHERE comment_id = c.id) AS like_count
      FROM comments c
      JOIN users u ON c.user_id = u.id
      WHERE c.post_id = ?
      ORDER BY c.created_at ASC
    `;
    const [comments] = await pool.query(commentsSql, [postId]);

    // Query untuk mendapatkan semua balasan terkait komentar
    const repliesSql = `
      SELECT r.id, r.content, r.created_at, r.parent_comment_id, u.username
      FROM replies r
      JOIN users u ON r.user_id = u.id
      WHERE r.parent_comment_id IN (SELECT id FROM comments WHERE post_id = ?)
      ORDER BY r.created_at ASC
    `;
    const [replies] = await pool.query(repliesSql, [postId]);

    // Gabungkan komentar dengan balasannya
    const commentsWithReplies = comments.map((comment) => {
      const commentReplies = replies
        .filter((reply) => reply.parent_comment_id === comment.id)
        .map((reply) => ({
          id: reply.id,
          content: reply.content,
          created_at: reply.created_at,
          username: reply.username,
          timeSinceReplied: `${Math.floor((Date.now() - new Date(reply.created_at)) / 60000)} minutes ago`,
        }));

      return {
        ...comment,
        timeSinceCommented: `${Math.floor((Date.now() - new Date(comment.created_at)) / 60000)} minutes ago`,
        replies: commentReplies,
      };
    });

    res.status(200).json({
      success: true,
      comments: commentsWithReplies,
    });
  } catch (error) {
    console.error('Error in getCommentsWithReplies:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to retrieve comments and replies',
    });
  }
};

//menambahkan like
const addLikeToComment = async (req, res) => {
  try {
    const { commentId } = req.params;
    const userId = req.user.id; // Mengambil userId dari token JWT

    // Cek apakah user sudah memberikan like pada komentar ini
    const checkLikeQuery = 'SELECT * FROM likes WHERE comment_id = ? AND user_id = ?';
    const [existingLike] = await pool.query(checkLikeQuery, [commentId, userId]);

    if (existingLike.length > 0) {
      return res.status(400).json({
        success: false,
        message: 'You have already liked this comment',
      });
    }

    // Jika belum ada like, tambahkan like
    const addLikeQuery = 'INSERT INTO likes (comment_id, user_id) VALUES (?, ?)';
    await pool.query(addLikeQuery, [commentId, userId]);

    // Update jumlah like di komentar
    const updateLikeCountQuery = 'UPDATE comments SET like_count = like_count + 1 WHERE id = ?';
    await pool.query(updateLikeCountQuery, [commentId]);

    res.status(200).json({
      success: true,
      message: 'Like added to comment successfully',
    });
  } catch (error) {
    console.error('Error adding like to comment:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to add like to comment',
    });
  }
};

// Menghapus like dari komentar
const removeLikeFromComment = async (req, res) => {
  try {
    const { commentId } = req.params;
    const userId = req.user.id; // Mengambil userId dari token JWT

    // Cek apakah user sudah memberikan like pada komentar ini
    const checkLikeQuery = 'SELECT * FROM likes WHERE comment_id = ? AND user_id = ?';
    const [existingLike] = await pool.query(checkLikeQuery, [commentId, userId]);

    if (existingLike.length === 0) {
      return res.status(400).json({
        success: false,
        message: 'You have not liked this comment yet',
      });
    }

    // Jika sudah ada like, hapus like
    const removeLikeQuery = 'DELETE FROM likes WHERE comment_id = ? AND user_id = ?';
    await pool.query(removeLikeQuery, [commentId, userId]);

    // Update jumlah like di komentar
    const updateLikeCountQuery = 'UPDATE comments SET like_count = like_count - 1 WHERE id = ?';
    await pool.query(updateLikeCountQuery, [commentId]);

    res.status(200).json({
      success: true,
      message: 'Like removed from comment successfully',
    });
  } catch (error) {
    console.error('Error removing like from comment:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to remove like from comment',
    });
  }
};

// Menampilkan semua komentar yang disukai oleh user
const getLikedCommentsByUser = async (req, res) => {
  try {
    const userId = req.user.id; // Mengambil userId dari token JWT

    // Query untuk mendapatkan komentar yang disukai oleh user
    const sql = `
      SELECT c.id, c.content, c.created_at, c.like_count, p.title AS post_title, p.id AS post_id
      FROM likes l
      JOIN comments c ON l.comment_id = c.id
      JOIN posts p ON c.post_id = p.id
      WHERE l.user_id = ?
      ORDER BY c.created_at DESC
    `;
    
    const [likedComments] = await pool.query(sql, [userId]);

    // Format waktu komentar yang disukai
    const formattedComments = likedComments.map(comment => ({
      ...comment,
      timeSinceLiked: `${Math.floor((Date.now() - new Date(comment.created_at)) / 60000)} minutes ago`
    }));

    res.status(200).json({
      success: true,
      likedComments: formattedComments,
    });
  } catch (error) {
    console.error('Error in getLikedCommentsByUser:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to retrieve liked comments',
    });
  }
};

// Menambahkan upvote pada postingan
const addUpvoteToPost = async (req, res) => {
  try {
    const { postId } = req.params;
    const userId = req.user.id; // Mendapatkan userId dari token JWT

    // Cek apakah pengguna sudah memberikan upvote atau downvote pada postingan ini
    const checkVoteQuery = 'SELECT vote_type FROM post_votes WHERE post_id = ? AND user_id = ?';
    const [existingVote] = await pool.query(checkVoteQuery, [postId, userId]);

    if (existingVote.length > 0) {
      if (existingVote[0].vote_type === 'upvote') {
        return res.status(400).json({ success: false, message: 'You have already upvoted this post' });
      } else if (existingVote[0].vote_type === 'downvote') {
        // Hapus downvote dan tambahkan upvote
        await pool.query('DELETE FROM post_votes WHERE post_id = ? AND user_id = ?', [postId, userId]);
      }
    }

    // Tambahkan upvote
    await pool.query('INSERT INTO post_votes (post_id, user_id, vote_type) VALUES (?, ?, ?)', [postId, userId, 'upvote']);
    await pool.query('UPDATE posts SET upvote_count = upvote_count + 1 WHERE id = ?', [postId]);

    res.status(200).json({ success: true, message: 'Upvote added successfully' });
  } catch (error) {
    console.error('Error adding upvote:', error);
    res.status(500).json({ success: false, message: 'Failed to add upvote' });
  }
};

// Menambahkan downvote pada postingan
const addDownvoteToPost = async (req, res) => {
  try {
    const { postId } = req.params;
    const userId = req.user.id; // Mendapatkan userId dari token JWT

    // Cek apakah pengguna sudah memberikan vote sebelumnya
    const checkVoteQuery = 'SELECT vote_type FROM post_votes WHERE post_id = ? AND user_id = ?';
    const [existingVote] = await pool.query(checkVoteQuery, [postId, userId]);

    if (existingVote.length > 0) {
      if (existingVote[0].vote_type === 'downvote') {
        return res.status(400).json({ success: false, message: 'You have already downvoted this post' });
      } else if (existingVote[0].vote_type === 'upvote') {
        // Hapus upvote dan tambahkan downvote
        await pool.query('DELETE FROM post_votes WHERE post_id = ? AND user_id = ?', [postId, userId]);
        await pool.query('UPDATE posts SET upvote_count = upvote_count - 1 WHERE id = ?', [postId]);
      }
    }

    // Tambahkan downvote
    await pool.query('INSERT INTO post_votes (post_id, user_id, vote_type) VALUES (?, ?, ?)', [postId, userId, 'downvote']);
    await pool.query('UPDATE posts SET downvote_count = downvote_count + 1 WHERE id = ?', [postId]);

    res.status(200).json({ success: true, message: 'Downvote added successfully' });
  } catch (error) {
    console.error('Error adding downvote:', error);
    res.status(500).json({ success: false, message: 'Failed to add downvote' });
  }
};

//retrieved vote status (single)
const getVoteStatus = async (req, res) => {
  try {
    const { postId } = req.params;
    const userId = req.user.id; // Mendapatkan userId dari token JWT

    // Query untuk memeriksa apakah pengguna sudah memberikan vote
    const checkVoteQuery = 'SELECT vote_type FROM post_votes WHERE post_id = ? AND user_id = ?';
    const [existingVote] = await pool.query(checkVoteQuery, [postId, userId]);

    if (existingVote.length > 0) {
      // Jika sudah ada vote, kembalikan jenis vote yang diberikan
      return res.status(200).json({
        success: true,
        message: 'Vote status retrieved successfully',
        vote_status: existingVote[0].vote_type, // Bisa "upvote" atau "downvote"
      });
    }

    // Jika belum ada vote, kembalikan status "no vote"
    res.status(200).json({
      success: true,
      message: 'User has not voted yet',
      vote_status: 'no vote',
    });
  } catch (error) {
    console.error('Error retrieving vote status:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to retrieve vote status',
    });
  }
};

// Hapus Upvote dari Postingan
const removeUpvoteFromPost = async (req, res) => {
  try {
    const { postId } = req.params;
    const userId = req.user.id; // Mendapatkan userId dari token JWT

    // Cek apakah user sudah memberikan upvote
    const checkVoteQuery = 'SELECT vote_type FROM post_votes WHERE post_id = ? AND user_id = ?';
    const [existingVote] = await pool.query(checkVoteQuery, [postId, userId]);

    if (existingVote.length === 0 || existingVote[0].vote_type !== 'upvote') {
      return res.status(400).json({
        success: false,
        message: 'You have not upvoted this post yet',
      });
    }

    // Hapus upvote dari tabel post_votes
    const removeUpvoteQuery = 'DELETE FROM post_votes WHERE post_id = ? AND user_id = ?';
    await pool.query(removeUpvoteQuery, [postId, userId]);

    // Update jumlah upvote pada post
    const updateUpvoteCountQuery = 'UPDATE posts SET upvote_count = upvote_count - 1 WHERE id = ?';
    await pool.query(updateUpvoteCountQuery, [postId]);

    res.status(200).json({
      success: true,
      message: 'Upvote removed from post successfully',
    });
  } catch (error) {
    console.error('Error removing upvote:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to remove upvote',
    });
  }
};

// Hapus Downvote dari Postingan
const removeDownvoteFromPost = async (req, res) => {
  try {
    const { postId } = req.params;
    const userId = req.user.id; // Mendapatkan userId dari token JWT

    // Cek apakah user sudah memberikan downvote
    const checkVoteQuery = 'SELECT vote_type FROM post_votes WHERE post_id = ? AND user_id = ?';
    const [existingVote] = await pool.query(checkVoteQuery, [postId, userId]);

    if (existingVote.length === 0 || existingVote[0].vote_type !== 'downvote') {
      return res.status(400).json({
        success: false,
        message: 'You have not downvoted this post yet',
      });
    }

    // Hapus downvote dari tabel post_votes
    const removeDownvoteQuery = 'DELETE FROM post_votes WHERE post_id = ? AND user_id = ?';
    await pool.query(removeDownvoteQuery, [postId, userId]);

    // Update jumlah downvote pada post
    const updateDownvoteCountQuery = 'UPDATE posts SET downvote_count = downvote_count - 1 WHERE id = ?';
    await pool.query(updateDownvoteCountQuery, [postId]);

    res.status(200).json({
      success: true,
      message: 'Downvote removed from post successfully',
    });
  } catch (error) {
    console.error('Error removing downvote:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to remove downvote',
    });
  }
};

const getAllVoteStatus = async (req, res) => {
  try {
    // Ambil semua votes beserta info terkait postingan dan pengguna
    const sqlPosts = `
    SELECT p.id, p.title, p.content, p.image_url, p.created_at, p.user_id, u.username
    FROM posts p
    JOIN users u ON p.user_id = u.id
    ORDER BY p.created_at DESC
  `;  
  const [posts] = await pool.query(sqlPosts);
  console.log(posts); // Cek apakah hasil query sesuai  

    if (votes.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'No votes found',
      });
    }

    res.status(200).json({
      success: true,
      votes,  // Mengirimkan semua vote yang ada
    });
  } catch (error) {
    console.error('Error in getAllVoteStatus:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to retrieve votes',
    });
  }
};


// Mendapatkan artikel berdasarkan ID
const getArticleById = async (req, res) => {
  try {
    const { id } = req.params;
    const sql = 'SELECT * FROM articles WHERE id = ?';
    const [article] = await pool.query(sql, [id]);

    if (article.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Article not found',
      });
    }

    res.status(200).json({
      success: true,
      message: 'Article fetched successfully',
      data: article[0],
    });
  } catch (error) {
    console.error('Error fetching article:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to fetch article',
    });
  }
};

// Mendapatkan semua artikel
const getAllArticles = async (req, res) => {
  try {
    const sql = 'SELECT * FROM articles ORDER BY published_at DESC';
    const [articles] = await pool.query(sql);

    res.status(200).json({
      success: true,
      message: 'Articles fetched successfully',
      data: articles,
    });
  } catch (error) {
    console.error('Error fetching articles:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to fetch articles',
    });
  }
};

module.exports = { 
  registerUser, 
  loginUser, 
  getAllUsers, 
  updateUserProfile, 
  deleteUserAccount,
  logoutUser,
  uploadProfilePhoto,
  getDashboardData,
  getUserProfile,
  createPost,
  getPosts,
  getPostById,
  addShareToPost,
  addComment,
  getComments,
  addReplyToComment,
  getCommentsWithReplies,
  addLikeToComment,
  removeLikeFromComment,
  getLikedCommentsByUser,
  addUpvoteToPost,
  addDownvoteToPost,
  removeUpvoteFromPost,
  removeDownvoteFromPost,
  getVoteStatus,
  getAllVoteStatus,
  getArticleById,
  getAllArticles };