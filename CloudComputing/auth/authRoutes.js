const express = require('express');
const { body } = require('express-validator');
const { 
    registerUser, 
    loginUser, 
    getAllUsers, 
    getLoggedInUser, 
    updateUserProfile, 
    deleteUserAccount } = require('./authController');
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
router.get('/me', getLoggedInUser);
router.put('/update', updateUserProfile);
router.delete('/delete', deleteUserAccount);

module.exports = router;
