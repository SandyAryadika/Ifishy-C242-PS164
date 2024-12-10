package com.ifishy.ui.activity.history

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.databinding.ActivityHistoryDetailBinding
import com.ifishy.databinding.ActivityResultBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
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

        Glide.with(this)
            .load(image)
            .placeholder(ColorDrawable(ContextCompat.getColor(this, R.color.shimmer)))
            .error(ColorDrawable(ContextCompat.getColor(this, R.color.shimmer)))
            .into(binding.picTaken)

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