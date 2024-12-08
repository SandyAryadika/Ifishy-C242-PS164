# Menggunakan image dasar Python
FROM python:3.12-slim

# Menentukan direktori kerja di dalam container
WORKDIR /app

# Menyalin file requirements.txt ke dalam container
COPY requirements.txt /app/

# Menginstal dependensi yang dibutuhkan
RUN pip install --no-cache-dir -r requirements.txt

# Menyalin seluruh proyek ke dalam container
COPY . /app/

# Menentukan port yang akan digunakan oleh aplikasi FastAPI
EXPOSE 8080

# Perintah untuk menjalankan aplikasi FastAPI menggunakan Uvicorn
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8080"]
