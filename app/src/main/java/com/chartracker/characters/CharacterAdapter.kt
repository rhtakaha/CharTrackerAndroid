package com.chartracker.characters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chartracker.database.CharacterEntity
import com.chartracker.databinding.ListItemCharacterBinding

class CharacterAdapter(private val clickListener: CharacterListener): ListAdapter<CharacterEntity, CharacterAdapter.CharacterViewHolder>(CharacterDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        return CharacterViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    //ViewHolder class to build a recyclerview item
    class CharacterViewHolder private constructor(private val binding: ListItemCharacterBinding): RecyclerView.ViewHolder(binding.root){
        companion object {
            fun from(parent: ViewGroup): CharacterViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCharacterBinding.inflate(layoutInflater, parent, false)
                return CharacterViewHolder(binding)
            }
        }

        fun bind(item: CharacterEntity, clickListener: CharacterListener) {
            binding.character = item
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
class CharacterDiffCallback: DiffUtil.ItemCallback<CharacterEntity>(){
    override fun areItemsTheSame(oldItem: CharacterEntity, newItem: CharacterEntity): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: CharacterEntity, newItem: CharacterEntity): Boolean {
        return oldItem == newItem
    }

}

class CharacterListener(val clickListener: (charName: String) -> Unit){
    fun onClick(character: CharacterEntity) = clickListener(character.name!!)

}