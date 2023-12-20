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

package ui.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import domain.model.BreedListModel
import kotlinx.collections.immutable.ImmutableList
import ui.components.BreedListItem
import ui.theme.size

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedListScreen(
    breeds: ImmutableList<BreedListModel>,
    onClick: (String) -> Unit,
    onFavorite: (String, Boolean) -> Unit,
    onSearchClick: () -> Unit,
    filterFavorites: Boolean,
    onFilterFavoritesClick: () -> Unit,
    state: LazyStaggeredGridState,
    paddingValues: PaddingValues,
) {
    val animatedFilterIconColor by animateColorAsState(
        targetValue = if (filterFavorites) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            LocalContentColor.current
        },
        label = "animation for favorite icon color",
    )

    val animatedFilterIconBackgroundColor by animateColorAsState(
        targetValue = if (filterFavorites) {
            MaterialTheme.colorScheme.primary
        } else {
            Transparent
        },
        label = "animation for favorite icon background",
    )

    Column(modifier = Modifier.padding(paddingValues)) {
        TopAppBar(
            title = {
            },
            actions = {
                IconButton(
                    onClick = onSearchClick,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "favorite",
                    )
                }
                IconButton(
                    onClick = onFilterFavoritesClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = animatedFilterIconBackgroundColor,
                        contentColor = animatedFilterIconColor,
                    ),
                ) {
                    Icon(
                        imageVector = if (filterFavorites) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Filled.FavoriteBorder
                        },
                        contentDescription = "favorite",
                    )
                }
            },
        )
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(size.staggeredGridItemMaxWidth),
            state = state,
            contentPadding = PaddingValues(size.medium),
            verticalItemSpacing = size.medium,
            horizontalArrangement = Arrangement.spacedBy(size.medium),
        ) {
            items(
                items = breeds,
                key = { it.id },
            ) { breed ->
                BreedListItem(
                    name = breed.name,
                    origin = breed.origin,
                    imageUrl = breed.imageUrl,
                    favorite = breed.favorite,
                    onClick = {
                        onClick(breed.id)
                    },
                    onFavoriteClick = {
                        onFavorite(breed.id, !breed.favorite)
                    },
                )
            }
        }
    }
}
