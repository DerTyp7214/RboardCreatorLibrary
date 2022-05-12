@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package de.dertyp7214.rboardcreatorlibrary.ui.components

import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.glide.GlideImage
import de.dertyp7214.rboardcreatorlibrary.R
import de.dertyp7214.rboardcreatorlibrary.data.Theme
import de.dertyp7214.rboardcreatorlibrary.ui.theme.RboardCreatorLibraryTheme
import de.dertyp7214.rboardcreatorlibrary.ui.theme.Typography

@Composable
fun ThemeRecyclerView(themes: List<Theme>, secondAction: (Theme) -> Unit) {
    RboardCreatorLibraryTheme {
        LazyColumn {
            items(items = themes, itemContent = {
                ListItem(it, secondAction)
            })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListItem(theme: Theme, secondAction: (Theme) -> Unit) {
    val shape = RoundedCornerShape(corner = CornerSize(8.dp))
    val context = LocalContext.current
    val customTabsIntent = CustomTabsIntent.Builder().setDefaultColorSchemeParams(
        CustomTabColorSchemeParams.Builder()
            .setToolbarColor(MaterialTheme.colorScheme.background.toArgb())
            .setSecondaryToolbarColor(MaterialTheme.colorScheme.background.toArgb())
            .setNavigationBarColor(MaterialTheme.colorScheme.background.toArgb()).build()
    ).setShowTitle(true).setInstantAppsEnabled(false).build()
    Surface(modifier = Modifier
        .padding(horizontal = 8.dp, vertical = 8.dp)
        .fillMaxWidth()
        .shadow(8.dp, shape)
        .combinedClickable(onClick = {
            customTabsIntent.launchUrl(
                context, Uri.parse(theme.url)
            )
        }, onLongClick = {
            secondAction(theme)
        }), color = MaterialTheme.colorScheme.surfaceVariant, shape = shape
    ) {
        Row {
            GlideImage(imageModel = theme.preview,
                modifier = Modifier
                    .padding(6.dp)
                    .width(100.dp)
                    .aspectRatio(810f / 540f),
                contentScale = ContentScale.Inside,
                requestOptions = {
                    RequestOptions().transform(CenterCrop(), RoundedCorners(32))
                },
                loading = {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                failure = {
                    CustomText(text = stringResource(R.string.error_loading_image))
                })
            Column(
                modifier = Modifier
                    .padding(
                        PaddingValues(
                            start = 4.dp, top = 8.dp, end = 8.dp, bottom = 8.dp
                        )
                    )
                    .align(Alignment.Top)
            ) {
                CustomText(text = theme.name, style = Typography.titleLarge, maxLines = 1)
                CustomText(text = theme.author, style = Typography.bodyMedium, maxLines = 1)
            }
        }
    }
}

@Composable
private fun CustomText(
    text: String, style: TextStyle = LocalTextStyle.current, maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        style = style,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = maxLines
    )
}