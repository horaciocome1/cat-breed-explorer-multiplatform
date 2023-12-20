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

package data.remote.impl

import data.remote.BreedsService
import data.remote.model.Breed
import data.remote.util.KtorService
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.path
import io.ktor.utils.io.core.use

private const val PATH_BREEDS = "breeds"
private const val PARAMETER_LIMIT = "limit"
private const val PARAMETER_PAGE = "page"

private const val PATH_BREEDS_SEARCH = "breeds/search"
private const val PARAMETER_QUERY = "q"

class BreedsServiceImpl(
    apiKey: String,
    apiHost: String,
    apiVersion: String,
) : KtorService(apiKey, apiHost, apiVersion), BreedsService {

    init {
        log.v(tag, "init")
    }

    override suspend fun getBreeds(limit: Int, page: Int): List<Breed> {
        log.v(tag, "getBreeds - limit=$limit page=$page")
        return httpClient.use { client ->
            client.get {
                url {
                    path(PATH_BREEDS)
                    parameter(PARAMETER_LIMIT, limit)
                    parameter(PARAMETER_PAGE, page)
                }
            }
        }.body()
    }

    override suspend fun getBreeds(name: String): List<Breed> {
        log.v(tag, "getBreeds - name=$name")
        return httpClient.use { client ->
            client.get {
                url {
                    path(PATH_BREEDS_SEARCH)
                    parameter(PARAMETER_QUERY, name)
                }
            }
        }.body()
    }
}
