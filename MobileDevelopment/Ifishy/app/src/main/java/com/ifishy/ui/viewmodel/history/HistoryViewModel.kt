package com.ifishy.ui.viewmodel.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifishy.data.model.history.ResponseHistory
import com.ifishy.data.model.history.ResponseHistoryById
import com.ifishy.data.repository.history.HistoryRepository
import com.ifishy.utils.ResponseState
import com.ifishy.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HistoryViewModel @Inject constructor(@Named("HistoryRepository") private val historyRepository: HistoryRepository) : ViewModel() {

    val saveScanHistory: MutableLiveData<SingleEvent<ResponseState<ResponseHistory>>> = MutableLiveData()
    val getScanHistory: MutableLiveData<ResponseState<ResponseHistoryById>> = MutableLiveData()

    fun saveScanHistory(fishImage: MultipartBody.Part, userId: RequestBody, disease: RequestBody, confidence: RequestBody){
        viewModelScope.launch {
            saveScanHistory.value = SingleEvent(ResponseState.Loading)

            val response = historyRepository.saveScanHistory(fishImage, userId, disease, confidence)
            saveScanHistory.postValue(response)
        }
    }

    fun getScanHistory(userId: Int){
        viewModelScope.launch {
            getScanHistory.value = ResponseState.Loading

            val response = historyRepository.getScanHistory(userId)
            getScanHistory.postValue(response)
        }
    }
}


