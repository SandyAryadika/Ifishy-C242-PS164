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
    getScanHistoryById 
} = require('./authController');

const router = express.Router();

router.post(
  '/register',
  [
    body('username').notEmpty().withMessage('Username is required'),
    body('email').isEmail().withMessage('Email is invalid'),
    body('password').isLength({ min: 6 }).withMessage('Password must be at least 6 characters long'),
  ],
  registerUser
);

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
router.post('/upload-photo', authenticateToken, upload.single('photo'), uploadProfilePhoto);
router.put('/update-photo-profile', authenticateToken, upload.single('image'), updatePhotoProfile);
router.get('/dashboard/:email', authenticateToken, getDashboardData);
router.get('/profile/:email', authenticateToken, getUserProfile);
router.post('/community/posts', authenticateToken, upload.single('image'), createPost); 
router.put('/community/posts/:postId', authenticateToken, upload.single('image'), updatePost);
router.get('/community/posts', authenticateToken, getPosts);
router.get('/community/posts/:id', authenticateToken, getPostById);
router.post('/community/posts/:postId/share', authenticateToken, addShareToPost);
router.post('/community/posts/:postId/comments', authenticateToken, addComment);
router.get('/community/posts/:postId/comments', authenticateToken, getComments);
router.get('/community/comments/:commentId', authenticateToken, getCommentsById);
router.post('/comments/:commentId/reply', authenticateToken, addReplyToComment);
router.get('/community/posts/:postId/comments', authenticateToken, getCommentsWithReplies);
router.post('/comments/:commentId/like', authenticateToken, addLikeToComment);
router.delete('/comments/:commentId/like', authenticateToken, removeLikeFromComment);
router.get('/users/likes', authenticateToken, getLikedCommentsByUser);
router.post('/community/posts/:postId/upvote', authenticateToken, addUpvoteToPost);
router.post('/community/posts/:postId/downvote', authenticateToken, addDownvoteToPost);
router.delete('/community/posts/:postId/upvote', authenticateToken, removeUpvoteFromPost);
router.delete('/community/posts/:postId/downvote', authenticateToken, removeDownvoteFromPost);
router.get('/community/posts/:postId/vote-status', authenticateToken, getVoteStatusById);
router.get('/articles', getAllArticles);
router.get('/articles/:id', getArticleById);
router.get('/article/scrape/:url', getArticleFromUrl);
router.post('/bookmark', authenticateToken, addBookmark);
router.delete('/bookmark', authenticateToken, removeBookmark);
router.get('/bookmarks', authenticateToken, getBookmarks);
router.get('/bookmark/:id', authenticateToken, getBookmarkById);
router.post('/scan-history', upload.single('fishImage'), saveScanHistory); 
router.get('/scan-history/:userId', getScanHistoryById);

module.exports = router;