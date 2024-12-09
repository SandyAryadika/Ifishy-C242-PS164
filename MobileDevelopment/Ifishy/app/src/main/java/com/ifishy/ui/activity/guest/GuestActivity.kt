package com.ifishy.ui.activity.guest

import android.os.Bundle
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
        enableEdgeToEdge()
        binding =ActivityGuestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportFragmentManager.commit {
           add(R.id.fragment_container,HomeGuestFragment())
        }

    }
}