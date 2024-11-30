package com.ifishy.ui.activity.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ifishy.R
import com.ifishy.data.model.auth.request.LoginRequest
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.ActivityLoginBinding
import com.ifishy.ui.activity.main.MainActivity
import com.ifishy.ui.viewmodel.AuthViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding:ActivityLoginBinding
    private val authViewModel by viewModels<AuthViewModel>()
    private val preferencesViewModel by viewModels<PreferenceViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.button.setOnClickListener(this)
        binding.signup.setOnClickListener(this)

    }

    private fun isNotValid(): Boolean{
        return binding.email.text.isNotEmpty() && binding.password.text!!.isNotEmpty()
    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.apply {

                this.loading.visibility=View.VISIBLE
                this.button.apply {
                    this.text = ""
                    this.isEnabled = false
                }
            }
        }else{
            binding.apply {
                this.loading.visibility = View.GONE
                this.button.apply {
                    this.text =  applicationContext.getString(R.string.login)
                    this.isEnabled = true
                }
            }
        }
    }

    private fun userLogin(data: LoginRequest){
        authViewModel.userLogin(data).apply {
            authViewModel.loginResponse.observe(this@LoginActivity){event->
                event.getContentIfNotHandled()?.let { response->
                    when(response){
                        is ResponseState.Loading -> {
                            isLoading(true)
                        }
                        is ResponseState.Success -> {
                            isLoading(false)
                            preferencesViewModel.saveToken(response.data.token!!)
                            toast("Success")
                            startActivity(Intent(this@LoginActivity,MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            )
                        }
                        is ResponseState.Error -> {
                            isLoading(false)
                            toast(response.message)
                        }
                    }
                }
            }
        }
    }

    private fun toast(msg : String){
        Toast.makeText(this@LoginActivity,msg,Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        when(v){
            binding.button->{
                val userData = LoginRequest(binding.email.text.toString(),binding.password.text.toString())
                if(isNotValid()){
                    userLogin(userData)
                }else{
                    toast("Email & Password Empty")
                }
            }
            binding.signup->{
                startActivity(Intent(this@LoginActivity,SignUpActivity::class.java))
                finish()
            }
        }
    }
}