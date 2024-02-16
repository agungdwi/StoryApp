package com.example.myapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.myapplication.data.room.StoryListRepository
import com.example.myapplication.remote.response.StoryList
import com.example.myapplication.remote.response.StoryListResponse
import com.example.myapplication.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StoryViewModel(storyListRepository: StoryListRepository): ViewModel() {

    val story: LiveData<PagingData<StoryList>> =
        storyListRepository.getStory().cachedIn(viewModelScope)

    private val _storyWithLocation = MutableLiveData<List<StoryList>>()
    val storyWithLocation: LiveData<List<StoryList>> = _storyWithLocation

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun getStoryWithLocation(token:String){
        _isLoading.postValue(true)
        val client = ApiConfig.getApiService().getStoryListLocation(token, 100)
        client.enqueue(object : Callback<StoryListResponse> {
            override fun onResponse(
                call: Call<StoryListResponse>,
                response: Response<StoryListResponse>
            ) {
                _isLoading.postValue(false)
                if(response.isSuccessful){
                    if(response.body()?.listStory?.isEmpty() == true){
                        _isEmpty.postValue(true)
                    }else{
                        _storyWithLocation.postValue(response.body()?.listStory)
                    }

                }else{
                    _isError.postValue("ERROR ${response.code()} : ${response.message()}")
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryListResponse>, t: Throwable) {
                _isLoading.postValue(false)
                _isError.postValue(t.message)
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "StoryViewModel"
    }
}