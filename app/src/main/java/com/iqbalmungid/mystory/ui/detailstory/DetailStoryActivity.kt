package com.iqbalmungid.mystory.ui.detailstory

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.iqbalmungid.mystory.R
import com.iqbalmungid.mystory.data.remote.response.Stories
import com.iqbalmungid.mystory.databinding.ActivityDetailStoryBinding
import jp.wasabeef.glide.transformations.BlurTransformation

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var viewModel: DetailViewModel
    private lateinit var stories: Stories

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stories = intent.getParcelableExtra(EX_STORY)!!
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]
        viewModel.setDetail(stories)

        binding.apply {
            val user = viewModel.listStory
            val lat = user.lat
            val lon = user.lon

            Glide.with(imgBg)
                .load(user.photoUrl)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(35, 4)))
                .into(imgBg)
            Glide.with(imgStory)
                .load(user.photoUrl)
                .into(imgStory)
            txtTitle.text = "${user.name} Story"
            txtName.text = user.name
            txtDesc.text = user.description
            txtTime.text = user.createdAt
            if (lat != 0.0 && lon != 0.0){
                txtLoc.text = getAddress(lat, lon)
            } else {
                txtLoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_off, 0, 0, 0)
                txtLoc.text = getString(R.string.error_location)
            }

            btnBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun getAddress(lat: Double, lon: Double): String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lon, 1)
        return list[0].getAddressLine(0)
    }

    companion object {
        const val EX_STORY = "story"
    }
}