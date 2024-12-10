package com.ifishy.ui.activity.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ifishy.R
import com.ifishy.data.model.auth.request.LoginRequest
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.ActivityLoginBinding
import com.ifishy.ui.activity.guest.GuestActivity
import com.ifishy.ui.activity.main.MainActivity
import com.ifishy.ui.viewmodel.auth.AuthViewModel
import com.ifishy.utils.Dialog
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding:ActivityLoginBinding
    private val authViewModel by viewModels<AuthViewModel>()
    private val preferencesViewModel by viewModels<PreferenceViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.button.setOnClickListener(this)
        binding.signup.setOnClickListener(this)
        binding.guest.setOnClickListener {
            startActivity(Intent(this,GuestActivity::class.java))
        }

        binding.password.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(pass: Editable?) {

                val password = pass.toString()

                if (password.isNotEmpty() && password.length < 6){
                    binding.passwordContainer.error = getString(R.string.password_kurang_dari_6_karakter)
                    binding.password.error =
                        getString(R.string.password_kurang_dari_6_karakter)
                }else{
                    binding.password.error =null
                    binding.passwordContainer.error = null
                }

            }

        })

        binding.email.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(email: Editable?) {

                val emails = email.toString()

                if (emails.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(emails).matches()) {
                    binding.email.error = getString(R.string.email_tidak_valid)
                } else {
                    binding.email.error = null
                }

            }

        })

    }

    private fun isNotValid(): Boolean {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            return false
        }

        if (binding.email.error != null || binding.password.error != null) {
            return false
        }

        return true
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
                            preferencesViewModel.saveToken(response.data.token!!,
                                binding.email.text.toString()
                            )
                            goTo(MainActivity::class.java)
                        }
                        is ResponseState.Error -> {
                            isLoading(false)
                            Dialog.messageDialog(supportFragmentManager, message = getString(R.string.login_error), desc = response.message)
                        }
                    }
                }
            }
        }
    }

    private fun goTo(to: Class<*>){
        startActivity(Intent(this@LoginActivity,to))
        finish()
    }
    override fun onClick(v: View?) {
        when(v){
            binding.button->{
                val userData = LoginRequest(binding.email.text.toString(),binding.password.text.toString())
                if(isNotValid()){
                    userLogin(userData)
                }else{
                    Dialog.messageDialog(supportFragmentManager, message = getString(R.string.login_error), desc = getString(R.string.field_empty))
                }
            }
            binding.signup->{
                goTo(SignUpActivity::class.java)
            }
        }
    }
}