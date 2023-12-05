package com.example.chartracker.stories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chartracker.database.StoriesEntity
import com.example.chartracker.databinding.ListItemStoryBinding

// implementing ListAdapter means it handles a lot for us
class StoryAdapter: ListAdapter<StoriesEntity, StoryAdapter.StoryViewHolder>(StoryDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    //ViewHolder class to build a recyclerview item
    class StoryViewHolder private constructor(private val binding: ListItemStoryBinding): RecyclerView.ViewHolder(binding.root){
        companion object {
            fun from(parent: ViewGroup): StoryViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemStoryBinding.inflate(layoutInflater, parent, false)
                return StoryViewHolder(binding)
            }
        }

         fun bind(item: StoriesEntity) {
             binding.story = item
             binding.executePendingBindings()
             //don't need these since doing data binding in the xml
//             binding.storyName.text = item.name
//             binding.storyGenre.text = item.genre
//             binding.storyType.text = item.type
//             binding.storyAuthor.text = item.author
         }

    }
}

// using Diff Util to manage when to remake an item because it changed
class StoryDiffCallback: DiffUtil.ItemCallback<StoriesEntity>(){
    override fun areItemsTheSame(oldItem: StoriesEntity, newItem: StoriesEntity): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: StoriesEntity, newItem: StoriesEntity): Boolean {
        return oldItem == newItem
    }

}