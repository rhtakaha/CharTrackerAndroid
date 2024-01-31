package com.chartracker.ui.story

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.StoriesEntity
import kotlinx.coroutines.launch

class AddEditStoryViewModel: ViewModel() {
    private val tag = "AddStoryVM"
    private val db = DatabaseAccess()

    private val _title = mutableStateOf("")
    val title: MutableState<String>
        get() = _title

    fun updateInputTitle(newTitle: String){
        _title.value = newTitle
    }

    private val _author = mutableStateOf("")
    val author: MutableState<String>
        get() = _author

    fun updateInputAuthor(newAuthor: String){
        _author.value = newAuthor
    }

    private val _genre = mutableStateOf("")
    val genre: MutableState<String>
        get() = _genre

    fun updateInputGenre(newGenre: String){
        _genre.value = newGenre
    }

    private val _type = mutableStateOf("")
    val type: MutableState<String>
        get() = _type

    fun updateInputType(newType: String){
        _type.value = newType
    }

    /*navigate back to stories event*/
    private val _navToStories = mutableStateOf(false)
    val navToStories: MutableState<Boolean>
        get() = _navToStories

    fun resetNavToStories(){
        _navToStories.value = false
    }

    /*function that calls a database access method to create the story in Firebase
        also calls navigation*/
    fun submitStory(story: StoriesEntity, localImageURI: Uri?){

        viewModelScope.launch {
            val filename = "story_${story.title}"
            if (localImageURI != null){
                // if we are adding an image
                db.addImage(filename, localImageURI)
                db.addImageDownloadUrlToStory(story, filename)
            }
            Log.i(tag, "Creation of new story initiated")
            db.createStory(story)
            _navToStories.value = true
        }
    }
}