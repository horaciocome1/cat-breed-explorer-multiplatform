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

package data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import data.db.PreferencesApi
import data.mappers.toBreed
import data.mappers.toEntity
import data.remote.BreedsService
import data.remote.model.Breed
import io.github.horaciocome.breedy.data.db.BreedQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import util.Loggable
import util.parallelMap

private const val LIMIT = 10
private const val KEY_LAST_PAGE = "last_page"
private const val FIRST_PAGE = -1

@OptIn(ExperimentalCoroutinesApi::class)
class BreedsRepository(
    private val preferencesApi: PreferencesApi,
    private val breedsService: BreedsService,
    private val breedQueries: BreedQueries,
    private val dispatcher: CoroutineDispatcher,
) : Loggable {

    private val runtimeCache = mutableMapOf<String, Breed>()
    private val runtimeCacheMutex = Mutex()

    val breeds = breedQueries.selectAll()
        .asFlow()
        .mapToList(dispatcher)
        .distinctUntilChanged()
        .mapLatest { entities ->
            entities.parallelMap { it.toBreed() }
        }
        .onEach { breeds ->
            for (breed in breeds) {
                runtimeCacheMutex.withLock {
                    runtimeCache[breed.id] = breed
                }
            }
        }
        .onEach { log.d(tag, "breeds - size=${it.size}") }
        .flowOn(dispatcher)

    init {
        log.v(tag, "init")
    }

    suspend fun saveBreed(breed: Breed) {
        log.v(tag, "saveBreed - breed=$breed")
        withContext(dispatcher) {
            breedQueries.insert(breed.toEntity())
        }
    }

    suspend fun fetchMoreBreeds() {
        log.v(tag, "fetchMoreBreeds")
        withContext(dispatcher) {
            val nextPage = preferencesApi.read(KEY_LAST_PAGE, FIRST_PAGE) + 1

            val result = breedsService.getBreeds(
                limit = LIMIT,
                page = nextPage,
            )

            log.d(tag, "fetchMoreBreeds - page=$nextPage result=${result.size}")

            if (result.isEmpty()) return@withContext

            result.forEach { breed ->
                launch { saveBreed(breed) }
            }

            preferencesApi.save(KEY_LAST_PAGE, nextPage)
        }
    }

    suspend fun searchByName(name: String): List<Breed> {
        log.v(tag, "searchByName - name=$name")
        return withContext(dispatcher) {
            val result = breedsService.getBreeds(name)

            log.d(tag, "name - result=${result.size}")

            result.forEach { breed ->
                runtimeCacheMutex.withLock {
                    runtimeCache[breed.id] = breed
                }
            }

            return@withContext result
        }
    }

    suspend fun searchByNameLocally(name: String): List<Breed> {
        log.v(tag, "searchByNameLocally - name=$name")
        return withContext(dispatcher) {
            return@withContext breedQueries.selectAllWithNameLike(name)
                .executeAsList()
                .also { log.d(tag, "searchByNameLocally - result=${it.size}") }
                .parallelMap { it.toBreed() }
        }
    }

    suspend fun getBreed(id: String): Breed? {
        log.v(tag, "getBreed - id=$id")
        return runtimeCacheMutex.withLock {
            runtimeCache[id].also { log.d(tag, "getBreed - result=$it") }
        }
    }

    suspend fun refreshLocalBreeds() {
        log.v(tag, "refreshLocalBreeds")
        withContext(dispatcher) {
            var previousPage = preferencesApi.read(KEY_LAST_PAGE, FIRST_PAGE)

            val networkItems = buildList {
                while (previousPage > FIRST_PAGE) {
                    this += coroutineScope {
                        async {
                            breedsService.getBreeds(LIMIT, previousPage)
                        }
                    }
                    previousPage--
                }
            }.awaitAll().flatten()

            val cacheItems = runtimeCacheMutex.withLock {
                runtimeCache.toList().parallelMap { it.second }
            }

            val (itemsToInsertOrUpdate, itemsToDelete) = compareLists(cacheItems, networkItems)

            log.d(
                tag,
                "refreshLocalBreeds - networkItems=${networkItems.size} " +
                    "cacheItems=${cacheItems.size} " +
                    "itemsToInsertOrUpdate=${itemsToInsertOrUpdate.size} " +
                    "itemsToDelete=${itemsToDelete.size}",
            )

            launch {
                launch {
                    for (breed in itemsToInsertOrUpdate) {
                        launch {
                            breedQueries.insert(breed.toEntity())
                        }
                    }
                }

                launch {
                    for (breed in itemsToDelete) {
                        launch {
                            breedQueries.delete(breed.id)
                        }
                    }
                }
            }
        }
    }

    private fun compareLists(cacheList: List<Breed>, networkList: List<Breed>): Pair<List<Breed>, List<Breed>> {
        log.v(tag, "compareLists - cacheList=$cacheList networkList=$networkList")
        val toInsertOrUpdate = mutableListOf<Breed>()
        val toDelete = mutableListOf<Breed>()

        for (networkItem in networkList) {
            val cacheItem = cacheList.find { it.id == networkItem.id }

            if (cacheItem == null) {
                toInsertOrUpdate += networkItem
            } else if (cacheItem != networkItem) {
                toInsertOrUpdate += networkItem
            }
        }

        for (cacheItem in cacheList) {
            if (networkList.none { it.id == cacheItem.id }) {
                toDelete += cacheItem
            }
        }

        return Pair(toInsertOrUpdate, toDelete).also { log.d(tag, "compareLists - toInsertOrUpdate=${toInsertOrUpdate.size} toDelete=${toDelete.size}") }
    }
}
