package com.ifishy.ui.dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.ifishy.R
import com.ifishy.databinding.FragmentConfirmDialogBinding

class ConfirmDialogFragment : DialogFragment() {

    private var _binding:FragmentConfirmDialogBinding?=null
    private val binding get() = _binding!!

    var onClickListener: (()->Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentConfirmDialogBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        arguments?.let {
            binding.okay.text = it.getString("okay") ?: ContextCompat.getString(requireActivity(),R.string.okay)
            binding.message.text = it.getString("title")
            binding.desc.text = it.getString("desc")
        }

        binding.cancel.setOnClickListener { dismiss() }
        binding.okay.setOnClickListener {
            onClickListener?.invoke()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}