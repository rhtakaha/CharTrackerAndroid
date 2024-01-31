package com.chartracker.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.R
import com.chartracker.database.StoriesEntity

//TODO need to make so when adding story/character the public url is added instead of just filename
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun EntityHolder(imageUrl: String?, entityName: String, modifier: Modifier = Modifier){
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(bottom = 8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = modifier
                .fillMaxWidth()
        ) {
            if (imageUrl != null) {
                GlideImage(
                    model = imageUrl,
                    contentDescription = null,
                    loading = placeholder(R.drawable.baseline_downloading_24),
                    failure = placeholder(R.drawable.baseline_broken_image_24),
//                contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            } else {
                Spacer(modifier = Modifier.size(120.dp))
            }
            Spacer(modifier = Modifier.width(40.dp))
            Text(
                text = entityName,
                style = MaterialTheme.typography.headlineLarge
            )
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
fun PreviewEntityHolderWithImage(){
    CharTrackerTheme {
        Surface {
            EntityHolder("https://th.bing.com/th/id/OIP.xJ2gfgHVXWPRDgBclINipgHaEK?rs=1&pid=ImgDetMain", "Gollum")
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
fun PreviewEntityHolderWithoutImage(){
    CharTrackerTheme {
        Surface {
            EntityHolder(null, "Gollum")
        }
    }
}

@Composable
fun EntityHolderList(stories: List<StoriesEntity>){
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        items(stories){story ->
             EntityHolder(
                 imageUrl = story.imagePublicUrl,
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
    val stories = listOf(StoriesEntity(title="Lord of the Rings", imagePublicUrl = ""), StoriesEntity(title = "Batman"))
    CharTrackerTheme {
        Surface {
            EntityHolderList(stories = stories)
        }
    }
}