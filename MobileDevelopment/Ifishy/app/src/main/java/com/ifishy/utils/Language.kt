package com.ifishy.utils

import android.content.Context
import java.util.Locale

object Language {

    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)

        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)

        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

}