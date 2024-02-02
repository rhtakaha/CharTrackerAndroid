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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.chartracker.database.DatabaseEntity
import com.chartracker.database.StoriesEntity

//TODO need to make so when adding story/character the public url is added instead of just filename
@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EntityHolder(imageUrl: String?, entityName: String, onClick: (String) -> Unit, modifier: Modifier = Modifier){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.small,
        onClick = { onClick(entityName)},
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp, top = 4.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
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
            EntityHolder("https://th.bing.com/th/id/OIP.xJ2gfgHVXWPRDgBclINipgHaEK?rs=1&pid=ImgDetMain",
                "Gollum",
                {})
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
            EntityHolder(null, "Gollum", {})
        }
    }
}

@Composable
fun EntityHolderList(entities: List<DatabaseEntity>){
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        items(entities){entity ->
             EntityHolder(
                 imageUrl = entity.imagePublicUrl,
                entityName = entity.name!!,
                 onClick = {})
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
    val stories = listOf(StoriesEntity(name="Lord of the Rings", imagePublicUrl = ""), StoriesEntity(name = "Batman"))
    CharTrackerTheme {
        Surface {
            EntityHolderList(entities = stories)
        }
    }
}