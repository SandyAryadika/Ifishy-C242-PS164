package com.ifishy.ui.dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ifishy.R
import com.ifishy.databinding.FragmentMessageDialogBinding

class MessageDialogFragment : DialogFragment() {

    private var _binding:FragmentMessageDialogBinding?=null
    private val binding get() = _binding!!

    var onClickListener: (()->Unit)?=null

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
        _binding = FragmentMessageDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        binding.message.text = arguments?.getString("message")
        binding.desc.text = arguments?.getString("desc")
        binding.okay.text = arguments?.getString("okay") ?: requireContext().getString(R.string.okay)

        binding.okay.setOnClickListener {
            onClickListener?.invoke() ?: dismiss()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}