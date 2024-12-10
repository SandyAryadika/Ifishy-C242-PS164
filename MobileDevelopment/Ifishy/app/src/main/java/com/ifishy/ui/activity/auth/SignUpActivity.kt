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
import com.ifishy.data.model.auth.request.SignUpRequest
import com.ifishy.databinding.ActivitySignUpBinding
import com.ifishy.ui.activity.guest.GuestActivity
import com.ifishy.ui.viewmodel.auth.AuthViewModel
import com.ifishy.utils.Dialog
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySignUpBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.password.addTextChangedListener(object : TextWatcher {
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

        binding.email.addTextChangedListener(object : TextWatcher {
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


        binding.signUpButton.setOnClickListener(this)
        binding.loginButton.setOnClickListener(this)
        binding.guest.setOnClickListener {
            startActivity(Intent(this, GuestActivity::class.java))
        }



    }

    private fun isValid(): Boolean {
        val emailValid = binding.email.text.isNotEmpty() && binding.email.error == null
        val usernameValid = binding.username.text.isNotEmpty()
        val passwordValid = binding.password.text!!.isNotEmpty() && binding.password.error == null
        val confirmPassValid = binding.confirmPass.text.isNotEmpty()

        return emailValid && usernameValid && passwordValid && confirmPassValid
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
                            Dialog.messageDialog(supportFragmentManager,
                                getString(R.string.signup_success),
                                getString(R.string.sign_up_success_desc), okay = applicationContext.getString(R.string.login), onClickListener = {
                                startActivity(
                                    Intent(this@SignUpActivity,LoginActivity::class.java)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                )
                            })
                        }
                        is ResponseState.Error -> {
                            isLoading(false)
                            Dialog.messageDialog(supportFragmentManager,
                                message = getString(R.string.signup_error),
                                desc = response.message
                            )
                        }
                    }
                }
            }
        }
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
                            confirmPassword = this.confirmPass.text.toString()
                        )
                    }
                    signUp(userData)
                }else{
                    Dialog.messageDialog(supportFragmentManager, message = getString(R.string.signup_error), desc = getString(
                        R.string.field_empty
                    ))
                }
            }
            binding.loginButton->{
                startActivity(Intent(this@SignUpActivity,LoginActivity::class.java))
                finish()
            }
        }
    }
}