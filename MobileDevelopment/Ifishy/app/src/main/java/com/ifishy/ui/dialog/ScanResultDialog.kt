package com.ifishy.ui.dialog

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ifishy.R
import com.ifishy.databinding.FragmentMessageDialogBinding
import com.ifishy.databinding.FragmentResultDialogBinding

class ScanResultDialog : DialogFragment() {

    private var _binding:FragmentResultDialogBinding?=null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        binding.image.setImageURI(Uri.parse(arguments?.getString("image_uri")))

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}