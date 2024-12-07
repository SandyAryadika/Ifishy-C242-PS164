# Menggunakan Node.js versi LTS yang stabil
FROM node:18-alpine

# Set working directory
WORKDIR /usr/src/app

# Salin file package.json dan package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Salin semua file ke dalam container
COPY . .

# Salin file .env ke dalam container
COPY .env /usr/src/app/.env

# Expose port yang digunakan aplikasi
EXPOSE 8080

# Tentukan perintah untuk menjalankan aplikasi
CMD ["node", "server.js"]