/*
 * Copyright 2023 Horácio Flávio Comé Júnior
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package di

import com.arkivanov.decompose.ComponentContext
import data.di.CompositionData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ui.details.BreedDetailsComponent
import ui.list.BreedListComponent
import ui.search.BreedSearchComponent

class CompositionRoot(
    componentContext: ComponentContext,
    apiKey: String,
    apiHost: String,
    apiVersion: String,
) : Composition, ComponentContext by componentContext {

    override val coroutineDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    private val data: CompositionData by lazy {
        CompositionData(
            apiKey = apiKey,
            apiHost = apiHost,
            apiVersion = apiVersion,
        )
    }

    val breedListComponent: BreedListComponent by lazy {
        BreedListComponent(
            breedsRepository = data.breedsRepository,
            favoriteBreedsRepository = data.favoriteBreedsRepository,
            componentContext = this,
            coroutineDispatcher = coroutineDispatcher,
        )
    }

    val breedSearchComponent: BreedSearchComponent by lazy {
        BreedSearchComponent(
            breedsRepository = data.breedsRepository,
            favoriteBreedsRepository = data.favoriteBreedsRepository,
            componentContext = this,
            coroutineDispatcher = coroutineDispatcher,
        )
    }

    private lateinit var _breedDetailsComponents: BreedDetailsComponent

    init {
        log.v(tag, "init")
    }

    fun getBreedDetailsComponent(breedId: String): BreedDetailsComponent {
        log.v(tag, "getBreedDetailsComponent - breedId=$breedId")

        if (::_breedDetailsComponents.isInitialized && _breedDetailsComponents.breedId == breedId) {
            log.d(tag, "getBreedDetailsComponent - reusing existing component")
            return _breedDetailsComponents
        }

        _breedDetailsComponents = BreedDetailsComponent(
            breedId = breedId,
            breedsRepository = data.breedsRepository,
            favoriteBreedsRepository = data.favoriteBreedsRepository,
            componentContext = this,
            coroutineDispatcher = coroutineDispatcher,
        )

        return _breedDetailsComponents
    }
}
