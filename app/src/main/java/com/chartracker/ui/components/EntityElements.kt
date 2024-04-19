package com.chartracker.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.R
import com.chartracker.database.CharacterEntity
import com.chartracker.database.DatabaseEntity
import com.chartracker.database.StoryEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun StoryDetails(story: StoryEntity){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (story.imagePublicUrl.value != null) {
                GlideImage(
                    model = story.imagePublicUrl.value,
                    contentDescription = stringResource(id = R.string.story_image_desc),
                    loading = placeholder(R.drawable.baseline_downloading_24),
                    failure = placeholder(R.drawable.baseline_broken_image_24),
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            } else {
                Spacer(modifier = Modifier.size(120.dp))
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(start = 40.dp)
            ) {
                if (story.author.value != ""){
                    Text(
                        text = story.author.value,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    )
                }
                if (story.genre.value != ""){
                    Text(
                        text = story.genre.value,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    )
                }
                if (story.type.value != ""){
                    Text(
                        text = story.type.value,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EntityHolder(imageUrl: String?, entityName: String, onClick: (String) -> Unit){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.small,
        onClick = { onClick(entityName)},
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (imageUrl != null) {
                GlideImage(
                    model = imageUrl,
                    contentDescription = stringResource(id = R.string.entity_image),
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

@Composable
fun EntityHolderList(
    adSpacing: Int,
    entities: List<DatabaseEntity>,
    modifier: Modifier=Modifier,
    onClick: (String) -> Unit,
    story: StoryEntity?=null,
    alphaSort: () -> Unit,
    reverseAlphaSort: () -> Unit,
    recentSort: () -> Unit,
    reverseRecentSort: () -> Unit,
    ){
    var elements = 0
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTopButton by remember{
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }
    Box(modifier= modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.End) {
            SortingMenu(
                alphaSort = alphaSort,
                reverseAlphaSort = reverseAlphaSort,
                recentSort = recentSort,
                reverseRecentSort = reverseRecentSort)
            LazyColumn(
                contentPadding = PaddingValues(4.dp),
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (story != null) {
                    item {
                        StoryDetails(story = story)
                    }
                }
                items(entities) { entity ->
                    EntityHolder(
                        imageUrl = entity.imagePublicUrl.value,
                        entityName = entity.name.value,
                        onClick = onClick
                    )
                    elements += 1
                    if (elements % adSpacing == 0){
                        // if have passed adSpacing
                        AdmobBanner()
                    }
                }
            }
            if (entities.size < adSpacing){
                // if less than
                AdmobBanner()
            }
        }
        if (showScrollToTopButton) {
            SmallFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowUpward,
                    contentDescription = stringResource(id = R.string.scroll_to_top)
                )
            }
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
fun PreviewStoryDetails() {
    CharTrackerTheme {
        Surface {
            StoryDetails(
                story =
                StoryEntity(
                    name = "Star Wars",
                    author = "George Lucas",
                    genre = "Sci-fi",
                    type = "Movie",
                    imagePublicUrl = ""
                )
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

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light Mode")
@Composable
fun PreviewEntityHolderListWithStoryDetails(){
    val characters = listOf(
        CharacterEntity(name ="Gandalf", imagePublicUrl = ""),
        CharacterEntity(name = "Frodo Baggins"),
        CharacterEntity(name = "Bilbo Baggins"),
        CharacterEntity(name = "Aragorn"),
        CharacterEntity(name = "Borimir"),
        CharacterEntity(name = "Gimli"),
        CharacterEntity(name = "Legolas"),
        CharacterEntity(name = "Merry"),
        CharacterEntity(name = "Pippin"),
        CharacterEntity(name = "Sam")
        )
    val story = StoryEntity(name = "Lord of the Rings",
        author = "JRR Tolkien",
        genre = "Epic Fantasy",
        type = "Book/Film")
    CharTrackerTheme {
        Surface {
            EntityHolderList(
                adSpacing = 4,
                entities = characters,
                onClick = {},
                story = story,
                alphaSort = {},
                reverseAlphaSort = {},
                recentSort = {},
                reverseRecentSort = {})
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
    val stories = listOf(StoryEntity(name="Lord of the Rings", imagePublicUrl = ""), StoryEntity(name = "Batman"))
    CharTrackerTheme {
        Surface {
            EntityHolderList(
                adSpacing = 5,
                entities = stories,
                onClick = {},
                alphaSort = {},
                reverseAlphaSort = {},
                recentSort = {},
                reverseRecentSort = {})
        }
    }
}