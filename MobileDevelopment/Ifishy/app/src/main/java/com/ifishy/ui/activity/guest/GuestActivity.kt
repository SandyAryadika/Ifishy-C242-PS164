package com.ifishy.ui.activity.guest

import android.os.Bundle
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.ifishy.R
import com.ifishy.databinding.ActivityGuestBinding
import com.ifishy.ui.fragment.home.HomeGuestFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GuestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGuestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding =ActivityGuestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.commit {
           add(R.id.fragment_container,HomeGuestFragment())
        }

    }
}