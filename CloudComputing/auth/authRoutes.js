const express = require('express');
const multer = require('multer');
const upload = multer({ storage: multer.memoryStorage() });
const { body } = require('express-validator');
const { authenticateToken } = require('../middleware/authMiddleware');
const { 
    registerUser, 
    loginUser, 
    getAllUsers,
    updateUser, 
    deleteUserAccount,
    logoutUser,
    uploadProfilePhoto,
    updatePhotoProfile,
    getDashboardData,
    getUserProfile,
    addShareToPost,
    createPost,
    getPosts,
    updatePost,
    getPostById,
    addComment,
    getCommentsById,
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
    getVoteStatusById,
    getArticleById,
    getAllArticles,
    getArticleFromUrl,
    addBookmark,
    removeBookmark,
    getBookmarks,
    getBookmarkById,
    saveScanHistory,
    getScanHistory,
    getScanHistoryById } = require('./authController');

    const router = express.Router();

// Validasi untuk registrasi
router.post(
  '/register',
  [
    body('username').notEmpty().withMessage('Username is required'),
    body('email').isEmail().withMessage('Email is invalid'),
    body('password').isLength({ min: 6 }).withMessage('Password must be at least 6 characters long'),
  ],
  registerUser
);

// Validasi untuk login
router.post(
  '/login',
  [
    body('email').isEmail().withMessage('Email is invalid'),
    body('password').notEmpty().withMessage('Password is required'),
  ],
  loginUser
);

router.get('/users', getAllUsers);
router.put('/update', updateUser);
router.delete('/delete', deleteUserAccount);
router.post('/logout', logoutUser);
router.post('/upload-photo',  authenticateToken, upload.single('photo'), uploadProfilePhoto);
router.put('/update-photo-profile', authenticateToken, upload.single('image'), updatePhotoProfile);
router.get('/dashboard/:email', authenticateToken, getDashboardData);
router.get('/profile/:email', authenticateToken, getUserProfile);
router.post('/community/posts', authenticateToken, upload.single('image'), createPost); 
router.put('/community/posts/:postId', authenticateToken, upload.single('image'), updatePost);
router.get('/community/posts', authenticateToken, getPosts); // Ambil semua posting
router.get('/community/posts/:id', authenticateToken, getPostById); // Ambil postingan berdasarkan ID
router.post('/community/posts/:postId/share', authenticateToken, addShareToPost); // Route untuk menambahkan share ke postingan
router.post('/community/posts/:postId/comments', authenticateToken, addComment); // Tambahkan komentar
router.get('/community/posts/:postId/comments', authenticateToken, getComments); // Ambil komentar pada postingan
router.get('/community/comments/:commentId', authenticateToken, getCommentsById); // Ambil komentar pada postingan melalui id postingan
router.post('/comments/:commentId/reply', authenticateToken, addReplyToComment); // Tambahkan reply ke komentar
router.get('/community/posts/:postId/comments', authenticateToken, getCommentsWithReplies); // Dapatkan semua komentar beserta balasannya
router.post('/comments/:commentId/like', authenticateToken, addLikeToComment); // Tambahkan like pada komentar
router.delete('/comments/:commentId/like', authenticateToken, removeLikeFromComment); // Hapus like dari komentar
router.get('/users/likes', authenticateToken, getLikedCommentsByUser);
router.post('/community/posts/:postId/upvote', authenticateToken, addUpvoteToPost); // Route untuk melakukan upvote pada postingan
router.post('/community/posts/:postId/downvote', authenticateToken, addDownvoteToPost); // Route untuk melakukan downvote pada postingan
router.delete('/community/posts/:postId/upvote', authenticateToken, removeUpvoteFromPost);
router.delete('/community/posts/:postId/downvote', authenticateToken, removeDownvoteFromPost);
router.get('/community/posts/:postId/vote-status', authenticateToken, getVoteStatusById);
router.get('/articles', getAllArticles);
router.get('/articles/:id', getArticleById);
router.get('/article/scrape/:url', getArticleFromUrl);
router.post('/bookmark', authenticateToken, addBookmark);  // Menambahkan bookmark
router.delete('/bookmark', authenticateToken, removeBookmark);  // Menghapus bookmark
router.get('/bookmarks', authenticateToken, getBookmarks);  // Mendapatkan semua bookmark
router.get('/bookmark/:id', authenticateToken, getBookmarkById);
router.post('/scan-history', saveScanHistory); // Endpoint untuk menyimpan scan history
router.get('/scan-history/:userId', getScanHistory); // Endpoint untuk mendapatkan scan history berdasarkan user ID
router.get('/scan-history/:userId', getScanHistoryById);

module.exports = router;
