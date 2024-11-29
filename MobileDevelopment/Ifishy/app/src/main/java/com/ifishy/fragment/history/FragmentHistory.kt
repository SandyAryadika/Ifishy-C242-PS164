package com.ifishy.fragment.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ifishy.R

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentHistory.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentHistory : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentHistory.
         */

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentHistory().apply {
                arguments = Bundle().apply {

                }
            }
    }
}