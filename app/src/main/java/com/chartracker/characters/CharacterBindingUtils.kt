package com.chartracker.characters

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.chartracker.R
import com.chartracker.database.CharacterEntity

@BindingAdapter("characterName")
fun TextView.setCharacterName(item: CharacterEntity?){
    item?.let {
        text = item.name
    }
}

@BindingAdapter("characterImage")
fun ImageView.setCharacterImage(item: CharacterEntity?){
    item?.let {
        setImageResource(R.drawable.ic_launcher_foreground)
    }
}