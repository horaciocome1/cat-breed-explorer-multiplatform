/*
 *    Copyright 2023 Horácio Flávio Comé Júnior
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.text.font.FontWeight
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import ui.theme.LightGrey
import ui.theme.size

@Composable
fun BreedListItem(
    name: String,
    origin: String,
    imageUrl: String,
    favorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(size.one, LightGrey),
        onClick = onClick,
    ) {
        Box {
            Column {
                KamelImage(
                    resource = asyncPainterResource(
                        data = imageUrl,
                        filterQuality = FilterQuality.Low,
                        block = {
                            coroutineContext = Dispatchers.IO
                        },
                    ),
                    contentDescription = "$name image",
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.padding(
                        top = size.large,
                        start = size.medium,
                        end = size.medium,
                    ),
                )
                Text(
                    text = "📍 $origin",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(
                        top = size.small,
                        start = size.medium,
                        end = size.medium,
                        bottom = size.large,
                    ),
                )
            }
            IconButton(
                onClick = onFavoriteClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier
                    .padding(size.small)
                    .align(Alignment.TopEnd),
            ) {
                Icon(
                    imageVector = if (favorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Filled.FavoriteBorder
                    },
                    contentDescription = "favorite",
                )
            }
        }
    }
}
