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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import domain.model.BreedListModel
import kotlinx.collections.immutable.toImmutableList
import ui.theme.BreedyTheme

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BreedListScreenPreview() {
    BreedyTheme {
        BreedListScreen(
            breeds = listOf(
                BreedListModel(
                    id = "1",
                    name = "Abyssinian",
                    origin = "Egypt",
                    imageUrl = "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
                    favorite = true,
                ),
            ).toImmutableList(),
            onClick = { },
            onFavorite = { _, _ -> },
            onSearchClick = { },
            filterFavorites = false,
            onFilterFavoritesClick = { },
            state = rememberLazyStaggeredGridState(),
            paddingValues = PaddingValues(),
        )
    }
}
