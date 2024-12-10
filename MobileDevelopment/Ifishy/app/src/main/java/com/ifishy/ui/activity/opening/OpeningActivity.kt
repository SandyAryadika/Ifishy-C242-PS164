package com.ifishy.ui.activity.opening

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ifishy.R
import com.ifishy.databinding.ActivityOpeningBinding
import com.ifishy.ui.activity.auth.LoginActivity
import com.ifishy.ui.activity.auth.SignUpActivity

class OpeningActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding:ActivityOpeningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityOpeningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.getStartedButton.setOnClickListener(this)
        binding.loginButton.setOnClickListener(this)

    }

    private fun goTo(to: Class<*>){
        val intent = Intent(this,to)
        startActivity(intent)
        finish()
    }

    override fun onClick(v: View?) {
        when(v){
            binding.getStartedButton->{
                goTo(SignUpActivity::class.java)
            }
            binding.loginButton->{
                goTo(LoginActivity::class.java)
            }
        }
    }
}