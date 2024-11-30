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
import com.ifishy.data.model.auth.request.SignUpRequest
import com.ifishy.databinding.ActivitySignUpBinding
import com.ifishy.ui.viewmodel.AuthViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySignUpBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.signUpButton.setOnClickListener(this)
        binding.loginButton.setOnClickListener(this)

    }

    private fun isValid(): Boolean {
        return binding.email.text.isNotEmpty() &&
                binding.username.text.isNotEmpty() &&
                binding.password.text!!.isNotEmpty() &&
                binding.confirmPassword.text.isNotEmpty()
    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.apply {
                this.loading.visibility = View.VISIBLE
                this.signUpButton.text = ""
                this.signUpButton.apply {
                    this.text =  ""
                    this.isEnabled = false
                }
            }
        }else{
            binding.apply {
                this.loading.visibility = View.GONE
                this.signUpButton.apply {
                    this.text =  applicationContext.getString(R.string.sign_up)
                    this.isEnabled = true
                }
            }
        }
    }

    private fun signUp(userData: SignUpRequest){
        authViewModel.signUp(userData).apply {
            authViewModel.signUpResponse.observe(this@SignUpActivity){event->
                event.getContentIfNotHandled()?.let { response->
                    when(response){
                        is ResponseState.Loading -> {
                            isLoading(true)
                        }
                        is ResponseState.Success -> {
                            isLoading(false)
                            toast("Sign Up success")
                            startActivity(Intent(this@SignUpActivity,LoginActivity::class.java))
                            finish()
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
        Toast.makeText(this@SignUpActivity,msg, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        when(v){
            binding.signUpButton->{
                if (isValid()){
                    val userData:SignUpRequest
                    binding.apply {
                        userData = SignUpRequest(
                            email = this.email.text.toString(),
                            username = this.username.text.toString(),
                            password = this.password.text.toString(),
                            confirmPassword = this.confirmPassword.text.toString()
                        )
                    }
                    signUp(userData)
                }else{
                    toast("Field Empty")
                }
            }
            binding.loginButton->{
                startActivity(Intent(this@SignUpActivity,LoginActivity::class.java))
                finish()
            }
        }
    }
}