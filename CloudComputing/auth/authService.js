const dataService = require('../data/dataService');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const { JWT_SECRET } = require('../config/secrets');

module.exports = {
    registerUser: async (username, password) => {
        const hashedPassword = await bcrypt.hash(password, 10);
        return dataService.createUser({ username, password: hashedPassword });
    },
    authenticateUser: async (username, password) => {
        const user = await dataService.findUserByUsername(username);
        if (!user) {
            throw new Error('User not found');
        }
        const isValidPassword = await bcrypt.compare(password, user.password);
        if (!isValidPassword) {
            throw new Error('Invalid password');
        }
        return jwt.sign({ id: user.id, username: user.username }, JWT_SECRET, {expiresIn : "2d"});
    },
};
