from fastapi import FastAPI, UploadFile, File, HTTPException
from model.utils import load_model, predict_disease
import uvicorn
import logging

app = FastAPI()

# Load model saat server diinisialisasi
model = load_model("model/ifishy_disease_model.h5")

@app.get("/")
def read_root():
    return {"message": "Welcome to the Ifishy API"}

@app.post("/predict")
async def predict(file: UploadFile = File(...)):
    try:
        if not file.content_type.startswith("image/"):
            raise HTTPException(status_code=400, detail="Uploaded file is not an image.")

        # Proses file
        contents = await file.read()
        prediction, confidence = predict_disease(model, contents)
        # Ambil informasi penyakit
        disease_info = get_disease_info(prediction)
        return {
            "disease": prediction,
            "confidence": f"{confidence:.2f}%",
            "details": disease_info,
            "message": "Prediction successful!"
        }
    except Exception as e:
        logging.error(f"Error during prediction: {e}")
        raise HTTPException(status_code=500, detail="Internal Server Error")

def get_disease_info(disease: str):
    """
    Fungsi untuk mendapatkan informasi penyakit (penyebab, rekomendasi pengobatan, dan validasi) berdasarkan nama penyakit.
    """
    # Informasi detail penyakit
    disease_info = {
        "Penyakit Bakteri - Aeromoniasis (Infeksi Aeromonas)": {
            "Penyebab": "Utamanya disebabkan oleh Aeromonas spp., tetapi juga dapat melibatkan Pseudomonas spp. atau bakteri lainnya.",
            "Rekomendasi Pengobatan": "Berikan antibiotik seperti oksitetrasiklin, perbaiki kualitas air.",
            "Validasi": "Informasi ini sering ditemukan dalam referensi umum kesehatan ikan."
        },
        "Penyakit Insang Bakteri": {
            "Penyebab": "Utamanya disebabkan oleh Flavobacterium branchiophilum, tetapi juga dapat melibatkan Aeromonas spp., Pseudomonas spp., atau Vibrio spp.",
            "Rekomendasi Pengobatan": "Gunakan antibiotik seperti kloramfenikol, perbaiki aerasi air.",
            "Validasi": "Informasi sesuai dengan literatur akuakultur."
        },
        "Penyakit Merah Bakteri": {
            "Penyebab": "Utamanya disebabkan oleh Aeromonas spp. atau Pseudomonas spp., tetapi juga dapat melibatkan bakteri lain yang menyerang jaringan tubuh ikan.",
            "Rekomendasi Pengobatan": "Gunakan antibiotik sulfonamida, lakukan perawatan luka lokal.",
            "Validasi": "Cek dengan ahli apakah spesifik bakteri ini memerlukan kombinasi antibiotik lain."
        },
        "Penyakit Jamur - Saprolegniasis (Infeksi Kapang Air)": {
            "Penyebab": "Utamanya disebabkan oleh kapang Saprolegnia spp., tetapi juga dapat melibatkan jenis kapang air lainnya seperti Achlya spp.",
            "Rekomendasi Pengobatan": "Gunakan fungisida seperti malachite green atau larutan garam.",
            "Validasi": "Penggunaan malachite green dibatasi di beberapa negara, periksa regulasi lokal."
        },
        "Ikan Sehat": {
            "Penyebab": "Tidak ada.",
            "Rekomendasi Pengobatan": "Tidak ada pengobatan yang diperlukan, lanjutkan perawatan normal.",
            "Validasi": "Informasi ini aman dan pasti benar."
        },
        "Penyakit Parasit": {
            "Penyebab": "Utamanya disebabkan oleh Ichthyophthirius multifiliis (penyakit bintik putih), tetapi juga dapat melibatkan parasit lain seperti Trichodina spp. atau Dactylogyrus spp.",
            "Rekomendasi Pengobatan": "Gunakan formalin, metil biru, atau larutan garam. Tingkatkan kualitas lingkungan.",
            "Validasi": "Penanganan bintik putih sering menggunakan formalin, pastikan dosis sesuai panduan."
        },
        "Penyakit Virus - Penyakit Ekor Putih": {
            "Penyebab": "Utamanya disebabkan oleh virus seperti Iridovirus atau Betanodavirus, tetapi virus lain juga bisa menyebabkan infeksi serupa.",
            "Rekomendasi Pengobatan": "Isolasi ikan yang terinfeksi dan perbaiki kualitas air.",
            "Validasi": "Tidak ada pengobatan langsung untuk virus, fokus pada pencegahan."
        }
    }


    # Ambil informasi penyakit
    return disease_info.get(
        disease,
        {
            "Penyebab": "No information available.",
            "Rekomendasi Pengobatan": "No treatment available.",
            "Validasi": "No validation information available."
        }
    )


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8080)
