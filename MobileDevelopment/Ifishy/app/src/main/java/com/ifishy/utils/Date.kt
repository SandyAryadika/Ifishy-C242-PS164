package com.ifishy.utils

import java.text.SimpleDateFormat
import java.util.Locale

object Date {

    fun format(inputDate: String): String {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }

    fun formatHistoryDate(apiDate: String?): String? {
        val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val targetDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val date = apiDate?.let { apiDateFormat.parse(it) }
        return date?.let { targetDateFormat.format(it) }
    }

    fun beautifyDate(dateString: String?): String? {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val targetFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())


        val date = dateString?.let { originalFormat.parse(it) }
        return date?.let { targetFormat.format(it) }

    }
}