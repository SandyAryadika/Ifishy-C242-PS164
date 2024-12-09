const { pool } = require("../config/dbConfig");
const bcrypt = require("bcrypt");

const createUser = async (username, email, password) => {
    try {
        const hashedPassword = await bcrypt.hash(password, 10);
        const query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        const [result] = await pool.execute(query, [username, email, hashedPassword]);

        console.log("Insert query result:", result);
        
        return { success: true };
    } catch (error) {
        console.error("Error in createUser:", error);
        return { success: false, message: "Failed to create user" };
    }
};

module.exports = { createUser };
