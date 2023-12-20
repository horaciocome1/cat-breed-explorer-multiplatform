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

package io.github.horaciocome1.breedy

import MainView
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import di.CompositionRoot
import io.github.horaciocome1.breedy.data.remote.util.SecureKeyLibrary
import ui.Configuration
import ui.ProvideAppComponents

class MainActivity : AppCompatActivity() {

    private val rootComponentContext: DefaultComponentContext by lazy {
        defaultComponentContext()
    }

    private val compositionRoot: CompositionRoot by lazy {
        CompositionRoot(
            componentContext = rootComponentContext,
            apiKey = SecureKeyLibrary.getApiKey(),
            apiHost = SecureKeyLibrary.getApiHost(),
            apiVersion = SecureKeyLibrary.getApiVersion(),
        )
    }

    private val stackNavigation: StackNavigation<Configuration> by lazy {
        StackNavigation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProvideAppComponents(
                componentContext = rootComponentContext,
                compositionRoot = compositionRoot,
                stackNavigation = stackNavigation,
            ) {
                MainView()
            }
        }
    }
}
