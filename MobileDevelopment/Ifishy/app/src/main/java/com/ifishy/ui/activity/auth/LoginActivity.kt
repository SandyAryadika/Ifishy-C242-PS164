package com.ifishy.ui.activity.auth

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
import com.ifishy.databinding.ActivityLoginBinding
import com.ifishy.ui.viewmodel.AuthViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding:ActivityLoginBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.button.setOnClickListener(this)

    }

    private fun isNotValid(): Boolean{
        return binding.email.text.isNullOrEmpty() && binding.password.text.isNullOrEmpty()
    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.apply {
                this.button.text=""
                this.loading.visibility=View.VISIBLE
                this.button.isEnabled = false
            }
        }else{
            binding.apply {
                this.loading.visibility = View.GONE
                this.button.text= applicationContext.getString(R.string.login)
                this.button.isEnabled = true
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
                            toast(response.data.token!!)
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
                if(!isNotValid()){
                    userLogin(userData)
                }else{
                    toast("Email & Password Empty")
                }
            }
            binding.signup->{

            }
        }
    }
}