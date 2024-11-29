package com.ifishy.ui.opening

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ifishy.R
import com.ifishy.databinding.ActivityOpeningBinding
import com.ifishy.ui.auth.SignUpActivity

class OpeningActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding:ActivityOpeningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOpeningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

            }
        }
    }
}