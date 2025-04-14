package com.pistartech.postureperfect.utils

import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Siru malayil on 12-04-2025.
 */

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun LocalGifImage(modifier: Modifier = Modifier, drawable: Int,
                  loop: Boolean = true) {
    val context = LocalContext.current
    var gifDrawable by remember { mutableStateOf<AnimatedImageDrawable?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val source = ImageDecoder.createSource(context.resources, drawable)
                val animatedDrawable = ImageDecoder.decodeDrawable(source) as? AnimatedImageDrawable
                animatedDrawable?.apply {
                    if (!loop) repeatCount = 1
                    start()
                    gifDrawable = this
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    gifDrawable?.let { gif ->
        AndroidView(
            modifier = modifier.size(200.dp),
            factory = { ImageView(context).apply {
                setImageDrawable(gif) } })
    }
}