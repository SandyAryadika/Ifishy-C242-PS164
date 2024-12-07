const dotenv = require('dotenv');
dotenv.config();
const cors = require('cors');
const express = require('express');
const bodyParser = require('body-parser');
const authRoutes = require('./auth/authRoutes');
const app = express();

// Middleware
app.use(cors());
app.use(bodyParser.json()); // Untuk parsing JSON
app.use(express.json()); // Untuk menangani JSON
app.use(express.urlencoded({ extended: true })); // Untuk x-www-form-urlencoded

app.get('/', (req, res) => {
  console.log('Root route accessed');
  res.send('Welcome to the API!');
});

// Daftarkan route
app.use('/api/auth', authRoutes);

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});