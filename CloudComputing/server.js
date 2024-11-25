require('dotenv').config();
const express = require('express');
const bodyParser = require('body-parser');
const authRoutes = require('./auth/authRoutes');
const app = express();

// Middleware
app.use(bodyParser.json()); // Untuk parsing JSON

// Daftarkan route
app.use('/api/auth', authRoutes);

const PORT = 8000;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
