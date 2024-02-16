package com.example.myapplication.ui.story

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityDetailStoryBinding
import com.example.myapplication.utils.getAddressName
import com.example.myapplication.utils.withDateFormat

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_title)

        Glide.with(this)
            .load(intent.getStringExtra(EXTRA_KEY_IMAGE))
            .into(binding.ivStory)
        binding.tvUsername.text = intent.getStringExtra(EXTRA_KEY_USERNAME)
        binding.tvStory.text = intent.getStringExtra(EXTRA_KEY_DESCRIPTION)
        binding.tvUploadDate.text = intent.getStringExtra(EXTRA_KEY_DATE)?.withDateFormat()


        try{
            val lat = intent.getStringExtra(EXTRA_KEY_LAT)?.toDouble()
            val lon = intent.getStringExtra(EXTRA_KEY_LON)?.toDouble()
            if(lat !=null && lon != null){
                binding.tvLocation.text = getAddressName(this, lat, lon)
                binding.tvLocation.visibility = View.VISIBLE
            }else {
                binding.tvLocation.visibility = View.GONE
            }
        }catch (e: Exception){
            binding.tvLocation.visibility = View.GONE

        }


    }

    companion object{
        const val EXTRA_KEY_IMAGE = "KEY_IMAGE"
        const val EXTRA_KEY_USERNAME = "KEY_USERNAME"
        const val EXTRA_KEY_DESCRIPTION = "KEY_DESCRIPTION"
        const val EXTRA_KEY_DATE = "KEY_DATE"
        const val EXTRA_KEY_LAT = "KEY_LAT"
        const val EXTRA_KEY_LON = "KEY_LON"

    }
}