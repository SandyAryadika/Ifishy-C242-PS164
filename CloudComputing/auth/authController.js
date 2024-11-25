const pool = require('../config/dbConfig');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');
const { JWT_SECRET } = require('../config/secrets');

// Register User
async function registerUser(req, res) {
  const { username, email, password, confirmPassword } = req.body;

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

const getLoggedInUser = async (req, res) => {
  try {
      const token = req.headers.authorization?.split(" ")[1];
      if (!token) {
          return res.status(401).json({
              success: false,
              message: "Access token missing",
          });
      }

      const decoded = jwt.verify(token, process.env.JWT_SECRET);
      const [rows] = await pool.query("SELECT id, username, email FROM users WHERE id = ?", [decoded.id]);

      if (rows.length === 0) {
          return res.status(404).json({
              success: false,
              message: "User not found",
          });
      }

      res.status(200).json({
          success: true,
          user: rows[0],
      });
  } catch (error) {
      console.error("Error in getLoggedInUser:", error);
      res.status(500).json({
          success: false,
          message: "Failed to fetch user info",
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

module.exports = { 
  registerUser, 
  loginUser, 
  getAllUsers, 
  getLoggedInUser, 
  updateUserProfile, 
  deleteUserAccount };