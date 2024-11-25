const { registerUser, loginUser } = require('../logic/userService');

const register = async (req, res) => {
  try {
    const { username, email, password } = req.body;
    const newUser = await registerUser(username, email, password);
    res.status(201).json({ message: 'User registered successfully!', user: newUser });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

const login = async (req, res) => {
  try {
    const { username, password } = req.body;
    const result = await loginUser(username, password);
    res.status(200).json(result);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
};

module.exports = { register, login };
