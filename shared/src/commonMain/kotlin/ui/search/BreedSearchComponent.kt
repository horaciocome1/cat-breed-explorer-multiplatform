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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.arkivanov.decompose.ComponentContext
import data.remote.model.Breed
import data.repository.BreedsRepository
import data.repository.FavoriteBreedsRepository
import domain.mappers.toListModel
import domain.model.BreedListModel
import io.ktor.utils.io.errors.IOException
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.Component
import util.parallelMap

private const val KEYSTROKE_DEBOUNCE_MILLIS = 1350L

@OptIn(FlowPreview::class)
class BreedSearchComponent(
    private val breedsRepository: BreedsRepository,
    private val favoriteBreedsRepository: FavoriteBreedsRepository,
    componentContext: ComponentContext,
    coroutineDispatcher: CoroutineDispatcher,
) : Component(componentContext, coroutineDispatcher) {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val searchResult = MutableStateFlow<List<Breed>>(emptyList())

    var keyword by mutableStateOf("")
        private set
    private val keywordFlow = snapshotFlow { keyword }

    private var setAsFavoriteJob: Job? = null
    private var unsetAsFavoriteJob: Job? = null

    init {
        log.v(tag, "init")
        watchKeyword()
        watchSearchResult()
    }

    fun updateKeyword(keyword: String) {
        this.keyword = keyword
    }

    fun setAsFavorite(id: String) {
        log.v(tag, "setAsFavorite - id=$id")
        if (setAsFavoriteJob?.isActive == true) {
            log.w(tag, "setAsFavorite - job is active")
            return
        }
        setAsFavoriteJob = componentScope.launch {
            launch {
                favoriteBreedsRepository.setAsFavorite(id)
            }
            launch {
                val breed = searchResult.value.find { it.id == id }
                if (breed != null) {
                    log.w(tag, "setAsFavorite - uncached bird set as favorite")
                    breedsRepository.saveBreed(breed)
                }
            }
        }
    }

    fun unsetAsFavorite(id: String) {
        log.v(tag, "unsetAsFavorite - id=$id")
        if (unsetAsFavoriteJob?.isActive == true) {
            log.w(tag, "unsetAsFavorite - job is active")
            return
        }
        unsetAsFavoriteJob = componentScope.launch {
            favoriteBreedsRepository.unsetAsFavorite(id)
        }
    }

    private fun watchKeyword() {
        log.v(tag, "watchKeyword")
        componentScope.launch {
            keywordFlow.onEach { clearResult(it.isNotBlank()) }
                .debounce(KEYSTROKE_DEBOUNCE_MILLIS)
                .onEach { keyword ->
                    if (keyword.isBlank()) {
                        return@onEach
                    }

                    search(keyword)
                }.collect()
        }
    }

    private fun clearResult(loading: Boolean) {
        log.v(tag, "clearResult - loading=$loading")
        searchResult.update { emptyList() }

        _uiState.update { currentState ->
            currentState.copy(
                loading = loading,
                breeds = emptyList<BreedListModel>().toImmutableList(),
            )
        }
    }

    private suspend fun search(keyword: String) {
        log.v(tag, "search - keyword=$keyword")
        try {
            searchResult.update {
                breedsRepository.searchByName(keyword)
            }
        } catch (e: IOException) {
            log.e(tag, "search", e)
            val breeds = breedsRepository.searchByNameLocally(keyword)
            if (breeds.isEmpty()) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = true,
                        errorMessage = e.message ?: "",
                    )
                }
            } else {
                searchResult.update { breeds }
            }
        } catch (e: Exception) {
            log.e(tag, "search", e)
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

    private fun watchSearchResult() {
        log.v(tag, "watchSearchResult")
        combine(searchResult, favoriteBreedsRepository.breeds) { breeds, favoriteBreeds ->
            log.d(tag, "watchSearchResult - breeds=${breeds.size} favoriteBreeds=${favoriteBreeds.size}")
            _uiState.update { currentState ->
                currentState.copy(
                    loading = false,
                    breeds = breeds.parallelMap { breed ->
                        val favorite = favoriteBreeds.firstOrNull { it.id == breed.id } != null
                        breed.toListModel(favorite)
                    }.toImmutableList(),
                )
            }
        }.launchIn(componentScope)
    }

    data class UiState(
        val loading: Boolean = false,
        val breeds: ImmutableList<BreedListModel> = emptyList<BreedListModel>().toImmutableList(),
        val error: Boolean = false,
        val errorMessage: String = "",
    )
}
