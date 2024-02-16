package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.utils.LoginPreferences
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: LoginPreferences):ViewModel() {
    fun saveUserPref(userToken: String, userUid: String, userName:String, userEmail: String) {
        viewModelScope.launch {
            pref.saveUserPref(userToken,userUid,userName,userEmail)
        }
    }

    fun clearUserPref() {
        viewModelScope.launch {
            pref.clearUserPref()
        }
    }

    fun getUserToken(): LiveData<String>{
        return pref.getUserToken().asLiveData()
    }

}