package com.ifishy.ui.activity.result

import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.ifishy.R
import com.ifishy.databinding.ActivityResultBinding
import com.ifishy.ui.viewmodel.scan.ScanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        val name = intent.getStringExtra(DISEASE_NAME)
        val cause = intent.getStringExtra(DISEASE_CAUSE)
        val treatment = intent.getStringExtra(DISEASE_TREATMENT)
        val image= intent.getStringExtra(DISEASE_IMAGE)
        val validation = intent.getStringExtra(VALIDATION)
        val percentage = intent.getStringExtra(PERCENTAGE)

        binding.picTaken.setImageURI(Uri.parse(image))

        binding.percentage.text = percentage
        binding.validationContent.text = validation
        binding.disease.text = name
        binding.causeContent.text = cause
        binding.tratementContent.text = treatment

    }


    companion object{

        const val PERCENTAGE = "disease_percentage"
        const val DISEASE_IMAGE = "disease_image"
        const val DISEASE_NAME = "disease_name"
        const val DISEASE_CAUSE = "disease_cause"
        const val DISEASE_TREATMENT = "disease_treatment"
        const val VALIDATION = "validation"

    }
}