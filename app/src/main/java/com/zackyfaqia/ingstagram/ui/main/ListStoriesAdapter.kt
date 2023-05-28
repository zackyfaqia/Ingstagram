package com.zackyfaqia.ingstagram.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zackyfaqia.ingstagram.data.response.story.StoryItem
import com.zackyfaqia.ingstagram.databinding.ItemStoryBinding

class ListStoriesAdapter(private val listStories: List<StoryItem>) :
    RecyclerView.Adapter<ListStoriesAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = listStories[position].photoUrl
        val name = listStories[position].name

        Glide.with(holder.itemView.context)
            .load(photo)
            .into(holder.binding.ivItemImage)

        holder.binding.tvItemUsername.text = name

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listStories[position])
        }

    }

    override fun getItemCount(): Int = listStories.size

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryItem)
    }
}


