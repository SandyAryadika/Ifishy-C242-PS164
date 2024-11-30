package com.ifishy

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.ui.activity.auth.LoginActivity
import com.ifishy.ui.activity.main.MainActivity
import com.ifishy.ui.activity.onboarding.OnBoardingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val preferencesViewModel by viewModels<PreferenceViewModel>()
    private lateinit var splash:SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            splash = installSplashScreen()
        }
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            splash.setKeepOnScreenCondition{true}
            preferencesViewModel.token.observe(this){token->
                lifecycleScope.launch {
                    delay(1000)
                    goTo(token)
                }
            }
        }else{
            preferencesViewModel.token.observe(this){token->
                lifecycleScope.launch {
                    delay(1000)
                    goTo(token)
                }
            }
        }
    }

    private fun redirect(to: Class<*>){
        startActivity(Intent(this,to))
        finish()
    }

    private fun goTo(token: String){
        if (token.isEmpty()){
            preferencesViewModel.isAlreadyLogin.observe(this){already->
                if(already){
                    redirect(LoginActivity::class.java)
                }else{
                    redirect(OnBoardingActivity::class.java)
                }
            }
        }else{
            redirect(MainActivity::class.java)
        }
    }

}