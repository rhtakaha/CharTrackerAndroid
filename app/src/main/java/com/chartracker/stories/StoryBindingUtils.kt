package com.chartracker.stories

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.chartracker.R
import com.chartracker.database.StoriesEntity

@BindingAdapter("storyName")
fun TextView.setStoryName(item: StoriesEntity?){
    item?.let {
        text = item.name
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

@BindingAdapter("storyImage")
fun ImageView.setStoryImage(item: StoriesEntity?){
    item?.let {
        setImageResource(R.drawable.ic_launcher_foreground)
    }
}