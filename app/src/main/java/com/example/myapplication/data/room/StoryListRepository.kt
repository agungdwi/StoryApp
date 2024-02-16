package com.example.myapplication.data.room

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.myapplication.data.database.StoryDatabase
import com.example.myapplication.data.database.StoryRemoteMediator
import com.example.myapplication.remote.response.StoryList
import com.example.myapplication.remote.retrofit.ApiService

class StoryListRepository (
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    ) {
        fun getStory(): LiveData<PagingData<StoryList>> {

            @OptIn(ExperimentalPagingApi::class)
            return Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
                pagingSourceFactory = {
//                QuotePagingSource(apiService)
                    storyDatabase.storyListDao().getAllStory()
                }
            ).liveData
        }
}