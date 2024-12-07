const mysql = require('mysql2/promise');

// Fungsi untuk mendapatkan konfigurasi database berdasarkan environment
const getDbConfig = () => {
  const isCloudRun = process.env.DB_SOCKET_PATH ? true : false;

  if (isCloudRun) {
    console.log("Using socket path for Cloud Run connection");
    return {
      socketPath: process.env.DB_SOCKET_PATH, // Koneksi melalui socket path
      user: process.env.DB_USER,
      password: process.env.DB_PASS,
      database: process.env.DB_NAME,
      waitForConnections: true,
      connectionLimit: 10,
      queueLimit: 0,
      connectTimeout: 10000, // 10 detik
    };
  } else {
    console.log("Using host for local connection");
    return {
      host: process.env.DB_HOST, // Koneksi melalui host lokal
      user: process.env.DB_USER,
      password: process.env.DB_PASS,
      database: process.env.DB_NAME,
      waitForConnections: true,
      connectionLimit: 10,
      queueLimit: 0,
      connectTimeout: 10000, // 10 detik
    };
  }
};

// Buat pool koneksi dengan konfigurasi yang sesuai
const pool = mysql.createPool(getDbConfig());

module.exports = pool;
