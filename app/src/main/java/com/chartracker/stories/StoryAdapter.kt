package com.chartracker.stories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chartracker.database.StoryEntity
import com.chartracker.databinding.ListItemStoryBinding

// implementing ListAdapter means it handles a lot for us
class StoryAdapter(private val clickListener: StoryListener): ListAdapter<StoryEntity, StoryAdapter.StoryViewHolder>(StoryDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
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

         fun bind(item: StoryEntity, clickListener: StoryListener) {
             binding.story = item
             binding.clickListener = clickListener
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
class StoryDiffCallback: DiffUtil.ItemCallback<StoryEntity>(){
    override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
        return oldItem == newItem
    }

}

class StoryListener(val clickListener: (storyTitle: String) -> Unit){
    fun onClick(story: StoryEntity) = clickListener(story.name!!)

}