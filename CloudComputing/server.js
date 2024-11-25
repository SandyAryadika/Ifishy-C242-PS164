const express = require('express');
const dotenv = require('dotenv');
dotenv.config();

const authRoutes = require('./auth/authRoutes'); // Path ke authRoutes.js

const app = express();
app.use(express.json());

app.use('/api/auth', authRoutes); // Endpoint untuk login dan register

const PORT = process.env.PORT || 8000;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
