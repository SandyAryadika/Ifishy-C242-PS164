const dotenv = require('dotenv');
dotenv.config();
const cors = require('cors');
const express = require('express');
const bodyParser = require('body-parser');
const authRoutes = require('./auth/authRoutes');
const app = express();

app.use(cors());
app.use(bodyParser.json());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use('/api/auth', authRoutes);

app.get('/', (req, res) => {
  console.log('Root route accessed');
  res.send('Welcome to the Ifihsy Backend API!');
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});