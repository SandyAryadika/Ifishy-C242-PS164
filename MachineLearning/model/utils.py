import tensorflow as tf
from PIL import Image
import numpy as np
import io

def load_model(model_path: str):
    """Memuat model CNN dari path."""
    return tf.keras.models.load_model(model_path)

def preprocess_image(image_bytes: bytes, model):
    """Preprocessing gambar sebelum prediksi."""
    # Dapatkan input size dari model
    input_shape = model.input_shape  # Biasanya berbentuk (None, height, width, channels)
    target_size = (input_shape[1], input_shape[2])  # Ambil height dan width

    # Buka file gambar
    image = Image.open(io.BytesIO(image_bytes))

    # Pastikan gambar memiliki 3 channel (RGB)
    if image.mode != "RGB":
        image = image.convert("RGB")

    # Resize gambar sesuai ukuran input model
    image = image.resize(target_size)

    # Normalisasi nilai pixel ke rentang [0, 1]
    image_array = np.array(image) / 255.0

    # Tambahkan dimensi batch (1, height, width, channels)
    return np.expand_dims(image_array, axis=0)

def predict_disease(model, image_bytes: bytes):
    """Prediksi penyakit ikan menggunakan model CNN."""
    # Preprocess gambar menggunakan input size dari model
    preprocessed_image = preprocess_image(image_bytes, model)

    # Lakukan prediksi
    predictions = model.predict(preprocessed_image)

    # Ambil kelas dengan probabilitas tertinggi
    predicted_class = np.argmax(predictions, axis=1)[0]

    # Hitung confidence level
    confidence = np.max(predictions) * 100

    # Label sesuai dengan output model (lama)
    old_class_labels = ["Bacterial diseases - Aeromoniasis", "Bacterial gill disease", 
                        "Bacterial Red disease","Fungal diseases Saprolegniasis", 
                        "Healthy Fish", "Parasitic diseases", 
                        "Viral diseases White tail disease"]

    # Label baru yang ingin ditampilkan
    new_class_labels = ["Penyakit Bakteri - Aeromoniasis (Infeksi Aeromonas)", 
                        "Penyakit Insang Bakteri", 
                        "Penyakit Merah Bakteri", 
                        "Penyakit Jamur - Saprolegniasis (Infeksi Kapang Air)", 
                        "Ikan Sehat", 
                        "Penyakit Parasit", 
                        "Penyakit Virus - Penyakit Ekor Putih"]

    # Pemetaan antara label lama dan label baru
    label_mapping = dict(zip(old_class_labels, new_class_labels))

    # Dapatkan label baru berdasarkan prediksi
    old_label = old_class_labels[predicted_class]
    new_label = label_mapping[old_label]

    # Modifikasi untuk saling bertukar antara "Bacterial Red disease" dan "Bacterial gill disease"
    if new_label == "Penyakit Insang Bakteri" and old_label == "Bacterial gill disease":
        new_label = "Penyakit Merah Bakteri"  # Ganti ke "Penyakit Insang Bakteri"
    elif new_label == "Penyakit Merah Bakteri" and old_label == "Bacterial Red disease":
        new_label = "Penyakit Insang Bakteri"  # Ganti ke "Penyakit Merah Bakteri"

    return new_label, confidence
