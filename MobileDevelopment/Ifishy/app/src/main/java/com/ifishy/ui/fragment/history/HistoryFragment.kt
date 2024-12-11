package com.ifishy.ui.fragment.history

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifishy.R
import com.ifishy.data.model.community.response.PostsItem
import com.ifishy.data.model.history.Data
import com.ifishy.data.model.history.DataItem
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.FragmentHistoryBinding
import com.ifishy.ui.activity.history.HistoryDetailActivity
import com.ifishy.ui.adapter.history.HistoryAdapter
import com.ifishy.ui.viewmodel.history.HistoryViewModel
import com.ifishy.ui.viewmodel.profile.ProfileViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding?=null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()
    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val adapter: HistoryAdapter by lazy { HistoryAdapter() }
    private var listHistory: List<DataItem>? = null
    private var searchValue: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceViewModel.token.observe(viewLifecycleOwner){token->
            preferenceViewModel.email.observe(viewLifecycleOwner){email->
                getProfileInfo(token,email)
            }
        }

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(search: Editable?) {
                search.let {
                    searchValue = search.toString()
                    if (search.isNullOrEmpty()) {
                        listHistory?.let {
                            adapter.submitData(it)
                        }
                    } else {
                        listHistory?.filter {
                            it.disease?.trim()?.contains(search.trim(), true) == true
                        }
                            ?.let { data -> adapter.submitData(data) }
                    }
                }
            }

        })


    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.loading.apply {
                this.startShimmer()
                this.visibility = View.VISIBLE
            }
            binding.error.visibility = View.GONE
            binding.history.visibility = View.GONE
        }else{
            binding.loading.apply {
                this.stopShimmer()
                this.visibility = View.GONE
            }
            binding.error.visibility = View.GONE
            binding.history.visibility = View.VISIBLE
        }
    }

    private fun getHistory(userId:Int){
        historyViewModel.getScanHistory(userId).apply {
            historyViewModel.getScanHistory.observe(viewLifecycleOwner){response->
                when(response){
                    is ResponseState.Loading -> {
                        isLoading(true)
                    }
                    is ResponseState.Success -> {
                        isLoading(false)
                        if (response.data.data?.isEmpty()!!){
                            binding.history.visibility = View.GONE
                            binding.error.apply {
                                this.visibility = View.VISIBLE
                                this.text = context.getString(R.string.belum_ada_history)
                            }
                        }else{
                            binding.history.adapter = adapter
                            binding.history.layoutManager = LinearLayoutManager(requireActivity())
                            adapter.submitData(response.data.data)
                            adapter.setOnItemClickListener(object : HistoryAdapter.OnClick{
                                override fun onItemClicked(item: DataItem) {
                                    startActivity(Intent(requireActivity(),HistoryDetailActivity::class.java)
                                        .putExtra(HistoryDetailActivity.DISEASE_NAME,item.disease)
                                        .putExtra(HistoryDetailActivity.PERCENTAGE,"${item.confidence}%")
                                        .putExtra(HistoryDetailActivity.DISEASE_CAUSE,item.description)
                                        .putExtra(HistoryDetailActivity.VALIDATION,item.validation.toString())
                                        .putExtra(HistoryDetailActivity.DISEASE_IMAGE,item.fishImage)
                                        .putExtra(HistoryDetailActivity.DISEASE_TREATMENT,item.treatment)
                                    )
                                }

                            })
                        }
                    }
                    is ResponseState.Error -> {
                        isLoading(false)
                        binding.error.visibility =View.VISIBLE
                        binding.error.text = response.message
                    }
                }
            }
        }
    }

    private fun getProfileInfo(token: String, email: String) {
        profileViewModel.getProfile(token, email).apply {
            profileViewModel.profile.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ResponseState.Loading -> {
                        isLoading(true)
                    }
                    is ResponseState.Success -> {
                        getHistory(response.data.profile?.id!!)
                    }
                    is ResponseState.Error -> {

                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}