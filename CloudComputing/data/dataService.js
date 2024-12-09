const db = require('../config/dbConfig');

module.exports = {
    createUser: async (user) => {
        const query = 'INSERT INTO users (username, password) VALUES (?, ?)';
        const [result] = await db.execute(query, [user.username, user.password]);
        return result.insertId;
    },
    findUserByUsername: async (username) => {
        const query = 'SELECT * FROM users WHERE username = ?';
        const [rows] = await db.execute(query, [username]);
        return rows[0];
    },
};
