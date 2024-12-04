package com.ifishy.utils

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.ifishy.ui.dialog.ConfirmDialogFragment
import com.ifishy.ui.dialog.MessageDialogFragment

object Dialog {

    fun messageDialog(
        fragmentManager: FragmentManager,
        message: String,
        desc: String,
        okay: String? = null,
        onClickListener: (() -> Unit)? = null
    ) {
        val bundle = Bundle()
        val messageDialog = MessageDialogFragment()
        bundle.putString("message", message)
        bundle.putString("desc", desc)
        bundle.putString("okay", okay)
        messageDialog.arguments = bundle
        onClickListener?.let {
            messageDialog.onClickListener = it
        }

        messageDialog.show(fragmentManager, messageDialog::class.java.simpleName)
    }

    fun confirmDialog(
        fragmentManager: FragmentManager,
        title: String,
        desc: String,
        okay: String? = null,
        onClickListener: (() -> Unit)
    ) {
        val bundle = Bundle()
        val confirmDialog = ConfirmDialogFragment()
        bundle.putString("title",title)
        bundle.putString("desc",desc)
        bundle.putString("okay",okay)
        confirmDialog.arguments = bundle
        confirmDialog.onClickListener = onClickListener

        confirmDialog.show(fragmentManager,confirmDialog::class.java.simpleName)
    }

}