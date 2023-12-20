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

package data.remote.model

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Breed(
    @SerialName("weight") val weight: Weight = Weight(),
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("cfa_url") val cfaUrl: String = "",
    @SerialName("vetstreet_url") val vetstreetUrl: String = "",
    @SerialName("vcahospitals_url") val vcahospitalsUrl: String = "",
    @SerialName("temperament") val temperament: String = "",
    @SerialName("origin") val origin: String = "",
    @SerialName("country_codes") val countryCodes: String = "",
    @SerialName("country_code") val countryCode: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("life_span") val lifeSpan: String = "",
    @SerialName("indoor") val indoor: Long = 0,
    @SerialName("lap") val lap: Long = 0,
    @SerialName("alt_names") val altNames: String = "",
    @SerialName("adaptability") val adaptability: Long = 0,
    @SerialName("affection_level") val affectionLevel: Long = 0,
    @SerialName("child_friendly") val childFriendly: Long = 0,
    @SerialName("dog_friendly") val dogFriendly: Long = 0,
    @SerialName("energy_level") val energyLevel: Long = 0,
    @SerialName("grooming") val grooming: Long = 0,
    @SerialName("health_issues") val healthIssues: Long = 0,
    @SerialName("intelligence") val intelligence: Long = 0,
    @SerialName("shedding_level") val sheddingLevel: Long = 0,
    @SerialName("social_needs") val socialNeeds: Long = 0,
    @SerialName("stranger_friendly") val strangerFriendly: Long = 0,
    @SerialName("vocalisation") val vocalisation: Long = 0,
    @SerialName("experimental") val experimental: Long = 0,
    @SerialName("hairless") val hairless: Long = 0,
    @SerialName("natural") val natural: Long = 0,
    @SerialName("rare") val rare: Long = 0,
    @SerialName("rex") val rex: Long = 0,
    @SerialName("suppressed_tail") val suppressedTail: Long = 0,
    @SerialName("short_legs") val shortLegs: Long = 0,
    @SerialName("wikipedia_url") val wikipediaUrl: String = "",
    @SerialName("hypoallergenic") val hypoallergenic: Long = 0,
    @SerialName("reference_image_id") val referenceImageId: String = "",
    @SerialName("image") val image: Image = Image(),
    @Transient val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
) {

    @Serializable
    data class Weight(
        @SerialName("imperial") val imperial: String = "",
        @SerialName("metric") val metric: String = "",
    )

    @Serializable
    data class Image(
        @SerialName("id") val id: String = "",
        @SerialName("width") val width: Long = 0,
        @SerialName("height") val height: Long = 0,
        @SerialName("url") val url: String = "",
    )
}
