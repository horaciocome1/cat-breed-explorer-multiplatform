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

package data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import data.mappers.toBreed
import io.github.horaciocome.breedy.data.db.FavoriteBreedEntity
import io.github.horaciocome.breedy.data.db.FavoriteBreedQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import util.Loggable
import util.parallelMap

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteBreedsRepository(
    private val favoriteBreedQueries: FavoriteBreedQueries,
    private val dispatcher: CoroutineDispatcher,
) : Loggable {

    val breeds = favoriteBreedQueries.selectAll()
        .asFlow()
        .mapToList(dispatcher)
        .distinctUntilChanged()
        .mapLatest { entries ->
            entries.parallelMap { it.toBreed() }
        }
        .onEach { log.d(tag, "breeds - size=${it.size}") }
        .flowOn(dispatcher)

    init {
        log.v(tag, "init")
    }

    suspend fun setAsFavorite(id: String) {
        log.v(tag, "setAsFavorite - id=$id")
        withContext(dispatcher) {
            favoriteBreedQueries.transaction {
                val favorite = FavoriteBreedEntity(id, Clock.System.now().toEpochMilliseconds())
                favoriteBreedQueries.insert(favorite)
            }
        }
    }

    suspend fun unsetAsFavorite(id: String) {
        log.v(tag, "unsetAsFavorite - id=$id")
        withContext(dispatcher) {
            favoriteBreedQueries.transaction {
                favoriteBreedQueries.deleteWithId(id)
            }
        }
    }

    fun isFavorite(id: String): Flow<Boolean> {
        log.v(tag, "isFavorite - id=$id")
        return favoriteBreedQueries
            .selectAllWithId(id)
            .asFlow()
            .mapToOneOrNull(dispatcher)
            .distinctUntilChanged()
            .mapLatest { it != null }
            .onEach { log.d(tag, "isFavorite - result=$it") }
            .flowOn(dispatcher)
    }
}
