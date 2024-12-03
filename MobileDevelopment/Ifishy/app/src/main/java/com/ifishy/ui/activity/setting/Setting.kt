package com.ifishy.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ifishy.R
import com.ifishy.databinding.ActivitySettingBinding
import com.ifishy.ui.activity.faq.FrequentlyAskMenu
import com.ifishy.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Setting : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private val viewModel: SettingsViewModel by viewModels()


    private val languages = listOf("EN(US)", "ID", "EN(UK)")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeSettings()

        binding.expandButton.setOnClickListener {
            val intent = Intent(this, FrequentlyAskMenu::class.java)
            startActivity(intent)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter

        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguage = languages[position]
                viewModel.saveSettings(
                    language = selectedLanguage,
                    notificationEnabled = binding.switchNotif.isChecked,
                    themeDark = binding.switchNight.isChecked
                )
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.switchNotif.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveSettings(
                language = binding.languageSpinner.selectedItem.toString(),
                notificationEnabled = isChecked,
                themeDark = binding.switchNight.isChecked
            )
        }

        binding.switchNight.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveSettings(
                language = binding.languageSpinner.selectedItem.toString(),
                notificationEnabled = binding.switchNotif.isChecked,
                themeDark = isChecked
            )
        }
    }

    private fun observeSettings() {
        viewModel.language.observe(this) { language ->
            if (language != null) {
                binding.languageSpinner.setSelection(getLanguagePosition(language))
            } else {
                binding.languageSpinner.setSelection(0)
            }
        }

        viewModel.notificationEnabled.observe(this) { isEnabled ->
            binding.switchNotif.isChecked = isEnabled
        }

        viewModel.themeDark.observe(this) { isDark ->
            binding.switchNight.isChecked = isDark
        }
    }

    private fun getLanguagePosition(language: String): Int {
        return languages.indexOf(language)
    }

    private fun getLanguageFromPosition(position: Int): String {
        return languages[position]
    }

}
