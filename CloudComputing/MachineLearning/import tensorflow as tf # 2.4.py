import tensorflow as tf  # Pastikan TensorFlow versi 2.4.1 atau lebih baru
from tensorflow.keras.models import load_model

# Path lengkap ke file model .h5
model_path = r'C:\Users\PC\Downloads\capstone bismillah\ifishy_disease_model.h5'

# Muat model dari file .h5
model = load_model(model_path)

# Konversi model ke format TensorFlow Lite
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

# Simpan file model TFLite
tflite_path = r'C:\Users\PC\Downloads\capstone bismillah\ifishy_disease_model.tflite'
with open(tflite_path, "wb") as f:
    f.write(tflite_model)

print(f"Model berhasil dikonversi dan disimpan di {tflite_path}!")
