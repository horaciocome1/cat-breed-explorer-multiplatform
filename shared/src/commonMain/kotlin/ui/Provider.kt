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

package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import di.CompositionRoot

val LocalComponentContext: ProvidableCompositionLocal<ComponentContext> = staticCompositionLocalOf { error("Root component context was not provided") }
val LocalCompositionRoot: ProvidableCompositionLocal<CompositionRoot> = staticCompositionLocalOf { error("Root composition was not provided") }
val LocalStackNavigation: ProvidableCompositionLocal<StackNavigation<Configuration>> = staticCompositionLocalOf { error("Stack navigation was not provided") }

@Composable
fun ProvideAppComponents(
    componentContext: ComponentContext,
    compositionRoot: CompositionRoot,
    stackNavigation: StackNavigation<Configuration>,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalComponentContext provides componentContext,
        LocalCompositionRoot provides compositionRoot,
        LocalStackNavigation provides stackNavigation,
        content = content,
    )
}
