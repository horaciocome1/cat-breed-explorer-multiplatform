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

package data.di

import data.db.DatabaseDriverFactory
import data.db.PreferencesApi
import data.remote.BreedsService
import data.remote.impl.BreedsServiceImpl
import data.repository.BreedsRepository
import data.repository.FavoriteBreedsRepository
import di.Composition
import io.github.horaciocome.breedy.data.db.AppDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class CompositionData(
    private val apiKey: String,
    private val apiHost: String,
    private val apiVersion: String,
) : Composition {

    override val coroutineDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    private val breedsService: BreedsService
        get() = BreedsServiceImpl(
            apiKey = apiKey,
            apiHost = apiHost,
            apiVersion = apiVersion,
        )

    private val databaseDriverFactory: DatabaseDriverFactory
        get() = DatabaseDriverFactory()

    private val database: AppDatabase by lazy {
        AppDatabase(
            driver = databaseDriverFactory.createDriver(),
        )
    }

    private val preferencesApi by lazy {
        PreferencesApi(database.preferenceQueries, coroutineDispatcher)
    }

    val breedsRepository: BreedsRepository by lazy {
        BreedsRepository(
            preferencesApi = preferencesApi,
            breedsService = breedsService,
            breedQueries = database.breedQueries,
            dispatcher = coroutineDispatcher,
        )
    }

    val favoriteBreedsRepository: FavoriteBreedsRepository by lazy {
        FavoriteBreedsRepository(
            favoriteBreedQueries = database.favoriteBreedQueries,
            dispatcher = coroutineDispatcher,
        )
    }

    init {
        log.v(tag, "init")
    }
}
