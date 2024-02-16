package com.example.myapplication

import com.example.myapplication.remote.response.StoryList
import kotlin.text.Typography.quote

object DataDummy {

    fun generateDummyQuoteResponse(): List<StoryList> {
        val items: MutableList<StoryList> = arrayListOf()
        for (i in 0..100) {
            val story = StoryList(
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "2022-02-22T22:22:22Z",
                "agung",
                "Description",
                "id $i",
            "-16.002",
                "-10.212"

            )
            items.add(story)
        }
        return items
    }
}