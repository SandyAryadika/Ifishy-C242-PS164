package com.ifishy.ui.fragment.date

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.ifishy.R
import java.util.Calendar
import java.util.Locale


class DatePickerFragment(
    private val onDateSelected: (String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val locale = Locale("in", "IN")
        Locale.setDefault(locale)

        val dialog = DatePickerDialog(
            requireContext(),
            R.style.DatePickerDialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->

                val formattedDate = "$selectedYear-${selectedMonth + 1}-${selectedDay}"
                onDateSelected(formattedDate)
            },
            year,
            month,
            day
        )

        return dialog
    }
}