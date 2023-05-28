package com.zackyfaqia.ingstagram.ui.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.zackyfaqia.ingstagram.R
import com.zackyfaqia.ingstagram.data.Constants
import com.zackyfaqia.ingstagram.data.response.story.StoryItem
import com.zackyfaqia.ingstagram.databinding.ActivityDetailStoryBinding

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<StoryItem>(EXTRA_STORY)
        val empty = StoryItem("", "", "", "", 0.0, "", 0.0)
        populateView(data ?: empty)
        playAnimation()

        val actionbar = supportActionBar
        actionbar!!.title = getString(R.string.detail_story)
        actionbar.setDisplayHomeAsUpEnabled(true)

    }

    private fun populateView(data: StoryItem) {
        Glide.with(this@DetailStoryActivity)
            .load(data.photoUrl)
            .into(binding.ivDetailImage)

        binding.tvDetailUsername.text = data.name
        binding.tvDetailDescription.text = data.description
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun playAnimation() {
        val aDetailImage =
            ObjectAnimator.ofFloat(binding.ivDetailImage, View.ALPHA, 1f).setDuration(300)
        val aDetailUsername =
            ObjectAnimator.ofFloat(binding.tvDetailUsername, View.ALPHA, 1f).setDuration(300)
        val aLetailDescription =
            ObjectAnimator.ofFloat(binding.tvDetailDescription, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(aDetailImage, aDetailUsername, aLetailDescription)
            startDelay = Constants.ANIMATION_DELAY
        }.start()
    }

    companion object {
        const val EXTRA_STORY = "EXTRA_STORY"
    }
}