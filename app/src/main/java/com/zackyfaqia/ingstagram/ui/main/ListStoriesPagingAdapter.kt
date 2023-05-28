package com.zackyfaqia.ingstagram.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.zackyfaqia.ingstagram.data.response.story.StoryItem
import com.zackyfaqia.ingstagram.databinding.ItemStoryBinding
import com.zackyfaqia.ingstagram.ui.detail.DetailStoryActivity

class ListStoriesPagingAdapter : PagingDataAdapter<StoryItem, ListStoriesPagingAdapter.ViewHolder>(
    DIFF_CALLBACK
) {
    class ViewHolder(var Binding: ItemStoryBinding) : RecyclerView.ViewHolder(Binding.root) {
        fun bind(dataStoriesst: StoryItem) {
            Binding.tvItemUsername.text = dataStoriesst.name
            Glide.with(itemView.context)
                .load(dataStoriesst.photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(Binding.ivItemImage)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_STORY, dataStoriesst.photoUrl)
                intent.putExtra(DetailStoryActivity.EXTRA_STORY, dataStoriesst.name)
                intent.putExtra(DetailStoryActivity.EXTRA_STORY, dataStoriesst.description)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val Binding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(Binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userStoryStories = getItem(position)
        if (userStoryStories != null) {
            holder.bind(userStoryStories)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryItem>() {
            override fun areItemsTheSame(
                oldItem: StoryItem,
                newItem: StoryItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryItem,
                newItem: StoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}

