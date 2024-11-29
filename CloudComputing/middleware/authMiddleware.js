const jwt = require('jsonwebtoken');
const secretKey = process.env.JWT_SECRET || 'eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImV4cCI6MTczMjQ2ODA5NSwiaWF0IjoxNzMyNDY4MDk1fQ.6TTulDMCqgp9hR9YTXY5crLIxKG1W_CSsf3e1DMhm5Q'; // Pastikan JWT_SECRET ada di file .env

// Middleware untuk verifikasi token JWT
const authenticateToken = (req, res, next) => {
  const authHeader = req.header('Authorization'); // Mengambil Authorization Header

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({
      success: false,
      message: 'Access denied, token missing or invalid',
    });
  }

  const token = authHeader.replace('Bearer ', ''); // Menghapus prefix "Bearer "

  try {
    const decoded = jwt.verify(token, secretKey); // Verifikasi token
    req.user = decoded; // Menyimpan informasi pengguna yang terdekode di req.user
    next(); // Melanjutkan ke route berikutnya
  } catch (error) {
    console.error('Invalid token', error);

    // Menangani error token berdasarkan tipe error
    if (error.name === 'TokenExpiredError') {
      return res.status(401).json({
        success: false,
        message: 'Token expired, please login again',
      });
    } else if (error.name === 'JsonWebTokenError') {
      return res.status(401).json({
        success: false,
        message: 'Invalid token, please login again',
      });
    } else {
      return res.status(500).json({
        success: false,
        message: 'An error occurred during token verification',
      });
    }
  }
};

module.exports = { authenticateToken };
