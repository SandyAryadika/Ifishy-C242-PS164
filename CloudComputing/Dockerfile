# Gunakan image Node.js resmi dari Docker Hub (pilih versi LTS yang stabil)
FROM node:18-slim

# Set working directory di dalam container
WORKDIR /usr/src/app

# Copy package.json dan package-lock.json untuk menginstal dependencies
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy seluruh kode aplikasi ke dalam container
COPY . .

# Expose port yang digunakan oleh aplikasi (pastikan port sesuai dengan server Anda)
EXPOSE 8080

# Menentukan perintah untuk menjalankan aplikasi
CMD ["node", "server.js"]
