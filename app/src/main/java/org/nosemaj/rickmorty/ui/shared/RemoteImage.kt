package org.nosemaj.rickmorty.ui.shared

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import org.nosemaj.rickmorty.R

@Composable
fun RemoteImage(imageUrl: String, contentDescription: String) {
    AsyncImage(
        model = imageUrl,
        contentScale = ContentScale.FillWidth,
        contentDescription = contentDescription,
        error = painterResource(id = R.drawable.error_drawable),
        placeholder = painterResource(id = R.drawable.placeholder_drawable),
        modifier = Modifier.fillMaxWidth()
    )
}
