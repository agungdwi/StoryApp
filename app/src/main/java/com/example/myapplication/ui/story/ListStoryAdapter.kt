package com.example.myapplication.ui.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ItemStoryBinding
import com.example.myapplication.remote.response.StoryList
import com.example.myapplication.utils.withDateFormat

class ListStoryAdapter : PagingDataAdapter<StoryList, ListStoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoryList) {
            binding.tvUsername.text = data.name
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(binding.ivStory)
            binding.tvStory.text = data.description
            binding.tvUploadDate.text = data.createdAt.withDateFormat()

            itemView.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivUser, "profile_pic"),
                        Pair(binding.tvUsername, "username"),
                        Pair(binding.ivStory, "image"),
                        Pair(binding.tvStory, "desc"),
                        Pair(binding.tvStory, "date")
                    )
                val intentDetail = Intent(itemView.context, DetailStoryActivity::class.java)
                intentDetail.putExtra(DetailStoryActivity.EXTRA_KEY_IMAGE, data.photoUrl)
                intentDetail.putExtra(DetailStoryActivity.EXTRA_KEY_USERNAME, data.name)
                intentDetail.putExtra(DetailStoryActivity.EXTRA_KEY_DATE, data.createdAt)
                intentDetail.putExtra(DetailStoryActivity.EXTRA_KEY_DESCRIPTION, data.description)
                intentDetail.putExtra(DetailStoryActivity.EXTRA_KEY_LAT, data.lat)
                intentDetail.putExtra(DetailStoryActivity.EXTRA_KEY_LON, data.lon)
                itemView.context.startActivity(intentDetail,optionsCompat.toBundle())
            }
        }


    }

    companion object {
         val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryList>() {
            override fun areItemsTheSame(oldItem: StoryList, newItem: StoryList): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryList, newItem: StoryList): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}