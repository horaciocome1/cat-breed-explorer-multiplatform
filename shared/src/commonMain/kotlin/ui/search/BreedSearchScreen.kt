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

package ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import domain.model.BreedListModel
import kotlinx.collections.immutable.ImmutableList
import ui.components.BreedListItem
import ui.theme.size

@Composable
fun BreedSearchScreen(
    navigateUp: () -> Unit,
    keyword: String,
    onKeywordChange: (String) -> Unit,
    breeds: ImmutableList<BreedListModel>,
    onClick: (String) -> Unit,
    onFavorite: (String, Boolean) -> Unit,
    state: LazyStaggeredGridState,
    paddingValues: PaddingValues,
) {
    Scaffold(
        topBar = {
            OutlinedTextField(
                value = keyword,
                onValueChange = onKeywordChange,
                textStyle = MaterialTheme.typography.bodyMedium,
                placeholder = {
                    Text(text = "Search")
                },
                singleLine = true,
                leadingIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "navigate up",
                        )
                    }
                },
                shape = CircleShape,
                modifier = Modifier
                    .padding(size.small)
                    .fillMaxWidth(),
            )
        },
        modifier = Modifier.padding(paddingValues),
    ) { values ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(size.staggeredGridItemMaxWidth),
            state = state,
            contentPadding = PaddingValues(size.medium),
            verticalItemSpacing = size.medium,
            horizontalArrangement = Arrangement.spacedBy(size.medium),
            modifier = Modifier.padding(values),
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
                        onFavorite(breed.id, breed.favorite)
                    },
                )
            }
        }
    }
}
