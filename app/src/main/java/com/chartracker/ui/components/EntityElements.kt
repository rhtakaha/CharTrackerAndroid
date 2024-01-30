package com.chartracker.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.R
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.StoriesEntity
import com.google.firebase.storage.StorageReference

//TODO need to make so when adding story/character the public url is added instead of just filename
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun EntityHolder(imageUri: StorageReference?, entityName: String, modifier: Modifier = Modifier){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .fillMaxWidth()) {
        if (imageUri != null){
            GlideImage(
                model = imageUri,
                contentDescription = null,
                loading = placeholder(R.drawable.baseline_downloading_24),
                failure = placeholder(R.drawable.baseline_broken_image_24),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(40.dp))
        }else{
            Spacer(modifier = Modifier.width(120.dp))
        }
        Text(
            text= entityName,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light Mode")
@Composable
fun PreviewEntityHolder(){
    CharTrackerTheme {
        Surface {
//            EntityHolder("https://th.bing.com/th/id/OIP.xJ2gfgHVXWPRDgBclINipgHaEK?rs=1&pid=ImgDetMain", "Gollum")
        }
    }
}

@Composable
fun EntityHolderList(stories: List<StoriesEntity>){
    val db = DatabaseAccess()
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        items(stories){story ->
             EntityHolder(
                imageUri = story.imageFilename?.let {filename -> db.getImageRef(filename) },
                entityName = story.title!!)
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light Mode")
@Composable
fun PreviewEntityHolderList(){
    CharTrackerTheme {
        Surface {
//            EntityHolderList()
        }
    }
}