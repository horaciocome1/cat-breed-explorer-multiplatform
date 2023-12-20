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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.arkivanov.decompose.ComponentContext
import data.repository.BreedsRepository
import data.repository.FavoriteBreedsRepository
import domain.mappers.toListModel
import domain.model.BreedListModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.Component
import util.parallelMap

class BreedListComponent(
    private val breedsRepository: BreedsRepository,
    private val favoriteBreedsRepository: FavoriteBreedsRepository,
    componentContext: ComponentContext,
    coroutineDispatcher: CoroutineDispatcher,
) : Component(componentContext, coroutineDispatcher) {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    var filterFavorites by mutableStateOf(false)
    private val filterFavoritesFlow = snapshotFlow { filterFavorites }

    private var fetchBreedsJob: Job? = null
    private var setAsFavoriteJob: Job? = null
    private var unsetAsFavoriteJob: Job? = null

    init {
        log.v(tag, "init")
        watchBreeds()
        refresh()
    }

    fun fetchBreeds() {
        log.v(tag, "fetchBreeds")
        if (fetchBreedsJob?.isActive == true) {
            log.w(tag, "fetchBreeds - job is active")
            return
        }
        fetchBreedsJob = componentScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    loading = true,
                    error = false,
                    errorMessage = "",
                )
            }

            try {
                breedsRepository.fetchMoreBreeds()
            } catch (e: Exception) {
                log.e(tag, "fetchBreeds", e)
                _uiState.update { currentState ->
                    currentState.copy(
                        error = true,
                        errorMessage = e.message ?: "",
                    )
                }
            } finally {
                _uiState.update { currentState ->
                    currentState.copy(loading = false)
                }
            }
        }
    }

    fun setAsFavorite(id: String) {
        log.v(tag, "setAsFavorite - id=$id")
        if (setAsFavoriteJob?.isActive == true) {
            log.w(tag, "setAsFavorite job is active")
            return
        }
        setAsFavoriteJob = componentScope.launch {
            favoriteBreedsRepository.setAsFavorite(id)
        }
    }

    fun unsetAsFavorite(id: String) {
        log.v(tag, "unsetAsFavorite - id=$id")
        if (unsetAsFavoriteJob?.isActive == true) {
            log.w(tag, "unsetAsFavorite job is active")
            return
        }
        unsetAsFavoriteJob = componentScope.launch {
            favoriteBreedsRepository.unsetAsFavorite(id)
        }
    }

    private fun watchBreeds() {
        log.v(tag, "watchBreeds")
        combine(
            breedsRepository.breeds,
            favoriteBreedsRepository.breeds,
            filterFavoritesFlow,
        ) { breeds, favoriteBreeds, filterFavorites ->
            log.d(tag, "watchBreeds - breeds=${breeds.size} favoriteBreeds=${favoriteBreeds.size} filterFavorites=$filterFavorites")
            _uiState.update { currentState ->
                currentState.copy(
                    loading = breeds.isEmpty(),
                    breeds = if (filterFavorites) {
                        favoriteBreeds.parallelMap { it.toListModel(favorite = true) }
                    } else {
                        breeds.parallelMap { breed ->
                            val favorite = favoriteBreeds.firstOrNull { it.id == breed.id } != null
                            breed.toListModel(favorite)
                        }
                    }.toImmutableList(),
                )
            }

            if (breeds.isEmpty()) {
                log.w(tag, "watchBreeds - breeds are empty")
                fetchBreeds()
            }
        }.launchIn(componentScope)
    }

    private fun refresh() {
        log.v(tag, "refresh")
        componentScope.launch {
            try {
                breedsRepository.refreshLocalBreeds()
            } catch (e: Exception) {
                log.e(tag, "refresh", e)
            }
        }
    }

    data class UiState(
        val loading: Boolean = true,
        val breeds: ImmutableList<BreedListModel> = emptyList<BreedListModel>().toImmutableList(),
        val error: Boolean = false,
        val errorMessage: String = "",
    )
}
