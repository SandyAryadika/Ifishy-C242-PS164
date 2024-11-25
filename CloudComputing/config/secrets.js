require('dotenv').config();

module.exports = {
  jwtSecret: process.env.JWT_SECRET || 'eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImV4cCI6MTczMjQ2ODA5NSwiaWF0IjoxNzMyNDY4MDk1fQ.6TTulDMCqgp9hR9YTXY5crLIxKG1W_CSsf3e1DMhm5Q',
  jwtExpiration: '1h', // Token valid for 1 hour
};
