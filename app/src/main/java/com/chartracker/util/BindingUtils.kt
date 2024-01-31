package com.chartracker.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chartracker.R
import com.chartracker.database.CharacterEntity
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.StoriesEntity

/* for character list items*/
@BindingAdapter("characterName")
fun TextView.setCharacterName(item: CharacterEntity?){
    item?.let {
        text = item.name
    }
}

/* for story info*/

@BindingAdapter("storyName")
fun TextView.setStoryName(item: StoriesEntity?){
    item?.let {
        text = item.title
    }
}

@BindingAdapter("storyGenre")
fun TextView.setStoryGenre(item: StoriesEntity?){
    item?.let {
        text = item.genre
    }
}

@BindingAdapter("storyType")
fun TextView.setStoryType(item: StoriesEntity?){
    item?.let {
        text = item.type
    }
}

@BindingAdapter("storyAuthor")
fun TextView.setStoryAuthor(item: StoriesEntity?){
    item?.let {
        text = item.author
    }
}

@BindingAdapter("setStoryImage")
fun setStoryImage(imageView: ImageView, story: StoriesEntity?){
    story?.let {
//        if(story.imageFilename != null){
//            val db = DatabaseAccess()
//
//            // Download directly from StorageReference using Glide
//            Glide.with(imageView.context)
//                .load(db.getImageRef(story.imageFilename.toString()))
//                .apply(RequestOptions()
//                    .placeholder(R.drawable.baseline_downloading_24) //TODO replace with loading animation
//                    .error(R.drawable.baseline_broken_image_24))
//                .into(imageView)
//        }
    }
}

@BindingAdapter("setCharacterImage")
fun setCharacterImage(imageView: ImageView, character: CharacterEntity?){
    character?.let {
        if(character.imageFilename != null){
            val db = DatabaseAccess()

            // Download directly from StorageReference using Glide
            Glide.with(imageView.context)
                .load(db.getImageRef(character.imageFilename.toString()))
                .apply(RequestOptions()
                    .placeholder(R.drawable.baseline_downloading_24) //TODO replace with loading animation
                    .error(R.drawable.baseline_broken_image_24))
                .into(imageView)
        }
    }
}