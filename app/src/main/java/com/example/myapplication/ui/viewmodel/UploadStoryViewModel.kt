package com.example.myapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.remote.response.UploadStoryResponse
import com.example.myapplication.remote.retrofit.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadStoryViewModel: ViewModel() {
    private val _uploadResponse = MutableLiveData<UploadStoryResponse>()
    val uploadResponse: LiveData<UploadStoryResponse> = _uploadResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun uploadStory(token:String, image: MultipartBody.Part, desc: RequestBody, lat:RequestBody?, lon:RequestBody?){
        _isLoading.postValue(true)
        val client = ApiConfig.getApiService().uploadNewStory(token, image,desc,lat,lon)
        client.enqueue(object : Callback<UploadStoryResponse> {
            override fun onResponse(
                call: Call<UploadStoryResponse>,
                response: Response<UploadStoryResponse>
            ) {
                _isLoading.postValue(false)
                if(response.isSuccessful){
                    _uploadResponse.postValue(response.body())
                }else{
                    _isError.postValue("ERROR ${response.code()} : ${response.message()}")
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UploadStoryResponse>, t: Throwable) {
                _isLoading.postValue(false)
                _isError.postValue(t.message)
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "UploadStoryViewModel"
    }
}