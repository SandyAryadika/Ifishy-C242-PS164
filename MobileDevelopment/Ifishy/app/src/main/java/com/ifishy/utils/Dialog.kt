package com.ifishy.utils

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.ifishy.ui.dialog.MessageDialogFragment

object Dialog {

    fun messageDialog(fragmentManager: FragmentManager, message:String,desc:String,okay:String?=null,onClickListener: (()->Unit)?=null){
        val bundle = Bundle()
        val messageDialog = MessageDialogFragment()
        bundle.putString("message",message)
        bundle.putString("desc",desc)
        bundle.putString("okay",okay)
        messageDialog.arguments = bundle
        onClickListener?.let {
            messageDialog.onClickListener = it
        }

        messageDialog.show(fragmentManager,MessageDialogFragment::class.java.simpleName)
    }

}