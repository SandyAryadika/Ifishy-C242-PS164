package com.ifishy.ui.viewmodel.scan

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifishy.data.model.classification.ClassificationResponse
import com.ifishy.data.repository.scan.ScanRepository
import com.ifishy.utils.ResponseState
import com.ifishy.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ScanViewModel @Inject constructor(@Named("ScanRepository") private val scanRepository: ScanRepository): ViewModel() {

    var imageScan: Uri?=null
    val predictResult: MutableLiveData<SingleEvent<ResponseState<ClassificationResponse>>> = MutableLiveData()

    fun predict(image: MultipartBody.Part){
        viewModelScope.launch {
            predictResult.value = SingleEvent(ResponseState.Loading)

            val response = scanRepository.predict(image)
            predictResult.postValue(response)
        }
    }


}