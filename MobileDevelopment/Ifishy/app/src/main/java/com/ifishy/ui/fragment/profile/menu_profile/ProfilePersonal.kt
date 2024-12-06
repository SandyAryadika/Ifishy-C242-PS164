package com.ifishy.ui.fragment.profile.menu_profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ifishy.databinding.FragmentProfilePersonalBinding


class ProfilePersonal : Fragment() {
    private lateinit var binding: FragmentProfilePersonalBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilePersonalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edit.setOnClickListener {
            Toast.makeText(context, "Edit feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.profileBookmark.setOnClickListener {
            Toast.makeText(context, "Bookmark List feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }


}