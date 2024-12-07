package com.ifishy.ui.fragment.profile.menu_profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ifishy.databinding.FragmentProfilePersonalBinding


class ProfilePersonalFragment : Fragment() {

    private var _binding: FragmentProfilePersonalBinding?=null
    private val binding get() = _binding!!

    companion object {
        private const val EMAIL = "user_email"

        fun newInstance(data: String): ProfilePersonalFragment {
            val fragment = ProfilePersonalFragment()
            val bundle = Bundle()
            bundle.putString(EMAIL, data)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePersonalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.email.text = arguments?.getString(EMAIL)

    }


    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }


}