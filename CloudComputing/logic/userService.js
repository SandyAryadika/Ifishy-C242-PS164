const db = require('../config/dbConfig');

const getUserById = (userId) => {
  console.log('Attempting to fetch user with ID:', userId);

  db.query('SELECT * FROM users WHERE id = ?', [userId], (err, result) => {
    if (err) {
      console.error('Error fetching user from database:', err);
    } else {
      console.log('User fetched successfully:', result);
    }
  });
};

module.exports = { getUserById };
