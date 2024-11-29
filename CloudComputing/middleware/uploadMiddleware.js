const multer = require('multer');

// Batasan ukuran file (2MB dalam byte)
const MAX_SIZE = 2 * 1024 * 1024;

// Filter jenis file (hanya gambar)
const fileFilter = (req, file, cb) => {
  const allowedMimeTypes = ['image/jpeg', 'image/png', 'image/gif'];
  if (allowedMimeTypes.includes(file.mimetype)) {
    cb(null, true);
  } else {
    cb(new Error('Invalid file type. Only JPEG, PNG, and GIF are allowed.'));
  }
};

// Konfigurasi Multer
const upload = multer({
  storage: multer.memoryStorage(), // Menggunakan memoryStorage agar bisa langsung diunggah ke GCS
  limits: { fileSize: 3 * 1024 * 1024 }, // Batasan ukuran file 5MB
  fileFilter: (req, file, cb) => {
    if (!file.mimetype.startsWith('image/')) {
      return cb(new Error('Only image files are allowed!'), false);
    }
    cb(null, true);
  }
});

// Middleware untuk menangani upload
const uploadMiddleware = (req, res, next) => {
  upload(req, res, (err) => {
    if (err) {
      if (err instanceof multer.MulterError) {
        // Kesalahan dari Multer (misalnya ukuran file terlalu besar)
        return res.status(400).json({ success: false, message: err.message });
      } else if (err) {
        // Kesalahan lain (misalnya jenis file tidak valid)
        return res.status(400).json({ success: false, message: err.message });
      }
    }
    next();
  });
};

module.exports = uploadMiddleware;
