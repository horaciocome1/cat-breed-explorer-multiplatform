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

package data.db

import io.github.horaciocome.breedy.data.db.PreferenceEntity
import io.github.horaciocome.breedy.data.db.PreferenceQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import util.Loggable

class PreferencesApi(
    private val preferenceQueries: PreferenceQueries,
    private val dispatcher: CoroutineDispatcher,
) : Loggable {

    init {
        log.v(tag, "init")
    }

    suspend fun save(key: String, value: Any) {
        log.v(tag, "save - key=$key value=$value")
        withContext(dispatcher) {
            preferenceQueries.transaction {
                when (value) {
                    is Int,
                    is Long,
                    is Float,
                    is String,
                    is Boolean,
                    -> preferenceQueries.insert(PreferenceEntity(key, value.toString()))
                    else -> log.w(tag, "save - preference type is not supported")
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <T> read(key: String, default: T): T {
        log.v(tag, "read - key=$key default=$default")
        return withContext(dispatcher) {
            val preference = preferenceQueries.transactionWithResult {
                preferenceQueries.selectWithKey(key).executeAsOneOrNull()
            }?.value_

            if (preference == null) {
                log.w(tag, "read - preference not found")
                return@withContext default
            }

            val result = when (default) {
                is Int -> preference.toIntOrNull()
                is Long -> preference.toLongOrNull()
                is Float -> preference.toFloatOrNull()
                is Boolean -> preference.toBooleanStrictOrNull()
                else -> {
                    log.w(tag, "read - preference type not supported")
                    preference
                }
            } ?: default

            return@withContext result as T
        }
    }

    @Suppress("unused")
    suspend fun delete(key: String) {
        log.v(tag, "delete - key=$key")
        withContext(dispatcher) {
            preferenceQueries.transaction {
                preferenceQueries.delete(key)
            }
        }
    }
}
