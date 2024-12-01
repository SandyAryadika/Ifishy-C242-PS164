package com.ifishy.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class FaqItem (
    val question : String,
    val answer : String,
    var isExpanded : Boolean = false
)

class ViewModelFAQ: ViewModel() {

    private val _faqlist = MutableLiveData<MutableList<FaqItem>>()
    val faqlist: LiveData<MutableList<FaqItem>> = _faqlist

    init {
        _faqlist.value = mutableListOf(
            FaqItem("Apa itu aplikasi iFishy?"," iFishy adalah aplikasi yang dirancang untuk membantu pengguna dalam mendeteksi penyakit pada ikan melalui fitur scan. Selain itu, aplikasi ini menyediakan artikel, forum komunitas, riwayat penyakit, dan profil pengguna."),
            FaqItem("Bagaimana cara menggunakan fitur scan penyakit pada ikan?", "Anda cukup mengambil atau mengunggah foto ikan menggunakan fitur scan di aplikasi. Sistem akan menganalisis gambar dan memberikan informasi mengenai kemungkinan penyakit yang diderita ikan." ),
            FaqItem("Apa saja yang bisa saya temukan di menu Home?","Menu Home menampilkan berbagai artikel dan post terkini yang relevan dengan kesehatan ikan, tips perawatan, dan informasi menarik lainnya tentang dunia ikan."),
            FaqItem("Bagaimana cara melihat riwayat scan yang pernah saya lakukan?","Riwayat scan penyakit dapat diakses melalui menu History. Di sana, Anda dapat melihat daftar hasil scan sebelumnya, lengkap dengan tanggal dan informasi terkait."),
            FaqItem("Apa saja fitur yang tersedia di menu Community?","Menu Community memungkinkan Anda membuat post seputar ikan dan penyakitnya, memberikan komentar pada post orang lain, serta memberikan upvote atau downvote untuk menilai relevansi atau kualitas sebuah topik."),
            FaqItem("Bisakah saya menyimpan post atau artikel dari aplikasi?","Saat ini, Anda bisa menandai artikel atau post untuk dibaca nanti. Cukup klik opsi Save yang tersedia di setiap artikel atau post."),
            FaqItem("Bagaimana cara memperbarui informasi di menu Profile?","Anda bisa memperbarui informasi di menu Profile dengan memilih tombol Edit Profile. Dari sana, Anda dapat mengubah data seperti nama, foto profil, dan informasi pribadi lainnya."),
            FaqItem("Apakah aplikasi ini memerlukan koneksi internet?", "Ya, iFishy memerlukan koneksi internet untuk mengakses fitur seperti scan penyakit, artikel, post komunitas, dan sinkronisasi data pengguna."),
            FaqItem("Apakah data yang saya unggah di aplikasi aman?", "Ya, iFishy menggunakan sistem enkripsi untuk melindungi data pengguna. Informasi pribadi dan hasil scan Anda hanya dapat diakses oleh Anda."),
            FaqItem("Bagaimana cara melaporkan bug atau memberikan masukan terkait aplikasi?", "Anda dapat melaporkan bug atau memberikan masukan melalui tim pengembang via email ")
        )

    }

    fun toggleFaqExpansion(index: Int) {
        _faqlist.value?.let { list ->
            list[index].isExpanded = !list[index].isExpanded
            _faqlist.value = list
        }
    }
}