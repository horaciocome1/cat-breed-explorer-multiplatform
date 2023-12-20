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

import com.arkivanov.decompose.ComponentContext
import data.repository.BreedsRepository
import data.repository.FavoriteBreedsRepository
import domain.mappers.toDetailsModel
import domain.model.BreedDetailsModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.Component

class BreedDetailsComponent(
    val breedId: String,
    private val breedsRepository: BreedsRepository,
    private val favoriteBreedsRepository: FavoriteBreedsRepository,
    componentContext: ComponentContext,
    coroutineDispatcher: CoroutineDispatcher,
) : Component(componentContext, coroutineDispatcher) {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private var setAsFavoriteJob: Job? = null
    private var unsetAsFavoriteJob: Job? = null

    init {
        log.v(tag, "init - breedId=$breedId")
        getBreed()
        watchFavorite()
    }

    fun setAsFavorite() {
        log.v(tag, "setAsFavorite")
        if (setAsFavoriteJob?.isActive == true) {
            log.w(tag, "setAsFavorite - job is active")
            return
        }
        setAsFavoriteJob = componentScope.launch {
            favoriteBreedsRepository.setAsFavorite(breedId)
        }
    }

    fun unsetAsFavorite() {
        log.v(tag, "unsetAsFavorite")
        if (unsetAsFavoriteJob?.isActive == true) {
            log.w(tag, "unsetAsFavorite - job is active")
            return
        }
        unsetAsFavoriteJob = componentScope.launch {
            favoriteBreedsRepository.unsetAsFavorite(breedId)
        }
    }

    private fun getBreed() {
        log.v(tag, "getBreed")
        componentScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    loading = false,
                    breed = breedsRepository.getBreed(breedId)?.toDetailsModel()
                        ?: BreedDetailsModel.EMPTY,
                )
            }
        }
    }

    private fun watchFavorite() {
        log.v(tag, "watchFavorite")
        componentScope.launch {
            favoriteBreedsRepository.isFavorite(breedId).collectLatest { favorite ->
                _uiState.update { currentState ->
                    currentState.copy(
                        favorite = favorite,
                    )
                }
            }
        }
    }

    data class UiState(
        val loading: Boolean = true,
        val breed: BreedDetailsModel = BreedDetailsModel.EMPTY,
        val favorite: Boolean = false,
    )
}
