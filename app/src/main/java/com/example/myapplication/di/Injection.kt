package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.data.database.StoryDatabase
import com.example.myapplication.data.room.StoryListRepository
import com.example.myapplication.remote.retrofit.ApiConfig
import com.example.myapplication.utils.LoginPreferences
import com.example.myapplication.utils.dataStore

object Injection {
    fun providePreferences(context: Context): LoginPreferences {
        return LoginPreferences.getInstance(context.dataStore)
    }

    fun provideRepository(context: Context): StoryListRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryListRepository (database, apiService)
    }
}