package com.ifishy.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ifishy.databinding.ActivitySettingBinding
import com.ifishy.ui.activity.auth.LoginActivity
import com.ifishy.ui.activity.faq.FrequentlyAskMenu
import com.ifishy.ui.viewmodel.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private val viewModel: SettingsViewModel by viewModels()


    private val languages = listOf("EN(US)", "ID", "EN(UK)")
    private var isUpdatingNightMode = false

    private fun logout(){
        viewModel.clearSession()

        val intent = Intent(this@SettingActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        supportActionBar?.hide()
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeSettings()


        binding.settingsLogout.setOnClickListener {
            logout()
        }

        binding.expandButton.setOnClickListener {
            val intent = Intent(this, FrequentlyAskMenu::class.java)
            startActivity(intent)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter

        binding.languageSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedLanguage = languages[position]
                    viewModel.saveSettings(
                        language = selectedLanguage,
                        themeDark = binding.switchNight.isChecked
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }



        binding.switchNight.setOnCheckedChangeListener { _, isChecked ->
            if (!isUpdatingNightMode && viewModel.themeDark.value != isChecked) {
                viewModel.saveSettings(
                    language = binding.languageSpinner.selectedItem.toString(),
                    themeDark = isChecked
                )

                AppCompatDelegate.setDefaultNightMode(
                    if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
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

            viewModel.themeDark.observe(this) { isDark ->
                binding.switchNight.isChecked = isDark
            }
    }

    private fun getLanguagePosition(language: String): Int {
        return languages.indexOf(language)
    }

}
