package dev.anilbeesetti.nextplayer.feature.videopicker.composables

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dev.anilbeesetti.nextplayer.feature.videopicker.VideoItem
import java.io.File

@Composable
fun VideoItemsPickerView(
    videoItems: List<VideoItem>,
    onVideoItemClick: (uri: Uri) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        items(videoItems) { mediaItem ->
            VideoItemView(
                videoItem = mediaItem,
                onClick = { onVideoItemClick(File(mediaItem.data).toUri()) }
            )
        }
    }
}