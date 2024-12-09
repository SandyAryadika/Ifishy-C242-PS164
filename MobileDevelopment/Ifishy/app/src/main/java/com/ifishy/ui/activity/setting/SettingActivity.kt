package com.ifishy.ui.activity.setting

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ifishy.R
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.ActivitySettingBinding
import com.ifishy.ui.activity.auth.LoginActivity
import com.ifishy.ui.activity.faq.FrequentlyAskMenu
import com.ifishy.utils.Dialog
import com.ifishy.utils.Language
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private val languages = listOf("EN", "IN")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        binding.settingsLogout.setOnClickListener {
            logout()
        }

        binding.expandButton.setOnClickListener {
            val intent = Intent(this, FrequentlyAskMenu::class.java)
            startActivity(intent)
        }



        preferenceViewModel.theme.observe(this) { isDark ->
            if (isDark != null) {
                binding.switchNight.isChecked = isDark
            }
            binding.switchNight.setOnCheckedChangeListener { _, isChecked ->
                val isDarkMode = preferenceViewModel.saveTheme(isChecked)
                AppCompatDelegate.setDefaultNightMode(
                    if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )

            }
        }

    }


    private fun logout() {
        Dialog.confirmDialog(
            supportFragmentManager, getString(R.string.are_you_sure),
            getString(R.string.want_to_logout)
        ) {
            preferenceViewModel.clearSession().apply {
                startActivity(
                    Intent(this@SettingActivity, LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                finish()
            }
        }
    }

}
