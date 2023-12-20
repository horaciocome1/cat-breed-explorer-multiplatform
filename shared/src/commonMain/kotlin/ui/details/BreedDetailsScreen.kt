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

package ui.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import ui.theme.LightGrey
import ui.theme.size

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedDetailsScreen(
    navigateUp: () -> Unit,
    name: String,
    imageUrl: String,
    description: String,
    temperament: String,
    origin: String,
    lifeSpan: String,
    favorite: Boolean,
    onFavoriteClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "navigate up",
                        )
                    }
                },
                title = {
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            Box {
                KamelImage(
                    resource = asyncPainterResource(
                        data = imageUrl,
                        filterQuality = FilterQuality.High,
                        block = {
                            coroutineContext = Dispatchers.IO
                        },
                    ),
                    contentDescription = "$name image",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth(),
                )
                IconButton(
                    onClick = onFavoriteClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(size.medium)
                        .border(
                            border = BorderStroke(size.one, LightGrey),
                            shape = CircleShape,
                        ),
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
            Text(
                text = "Meet the $name from $origin with a life span of $lifeSpan years",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(
                    top = size.large,
                    end = size.medium,
                    start = size.medium,
                ),
            )
            Text(
                text = "About",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                ),
                modifier = Modifier.padding(
                    top = size.large,
                    end = size.medium,
                    start = size.medium,
                ),
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(
                    top = size.small,
                    end = size.medium,
                    start = size.medium,
                ),
            )
            Text(
                text = "Temperament",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                ),
                modifier = Modifier.padding(
                    top = size.large,
                    end = size.medium,
                    start = size.medium,
                ),
            )
            Text(
                text = temperament,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(
                    top = size.small,
                    end = size.medium,
                    bottom = size.extraLarge,
                    start = size.medium,
                ),
            )
        }
    }
}
