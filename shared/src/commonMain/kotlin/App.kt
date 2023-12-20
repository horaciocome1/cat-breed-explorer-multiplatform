
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import ui.ChildStack
import ui.Configuration
import ui.LocalCompositionRoot
import ui.LocalStackNavigation
import ui.details.BreedDetailsScreen
import ui.list.BreedListScreen
import ui.search.BreedSearchScreen

@Composable
fun App() {
    MaterialTheme {
        val compositionRoot = LocalCompositionRoot.current
        val navigation = LocalStackNavigation.current

        ChildStack(
            source = navigation,
            initialStack = {
                listOf(Configuration.List)
            },
            handleBackButton = true,
            animation = stackAnimation(fade() + scale()),
            modifier = Modifier.fillMaxSize(),
        ) { screen ->
            when (screen) {
                is Configuration.Detail -> {
                    val component = remember(screen.id) { compositionRoot.getBreedDetailsComponent(screen.id) }

                    val state by component.uiState.collectAsState()

                    BreedDetailsScreen(
                        navigateUp = navigation::pop,
                        name = state.breed.name,
                        description = state.breed.description,
                        temperament = state.breed.temperament,
                        origin = state.breed.origin,
                        lifeSpan = state.breed.lifeSpan,
                        imageUrl = state.breed.imageUrl,
                        favorite = state.favorite,
                        onFavoriteClick = {
                            if (state.favorite) {
                                component.unsetAsFavorite()
                            } else {
                                component.setAsFavorite()
                            }
                        },
                    )
                }

                Configuration.List -> {
                    val component = remember { compositionRoot.breedListComponent }

                    val state by component.uiState.collectAsState()

                    val listState = rememberLazyStaggeredGridState()

                    val snackbarHostState = remember { SnackbarHostState() }

                    LaunchedEffect(listState.canScrollForward) {
                        if (!listState.canScrollForward && !component.filterFavorites) {
                            component.fetchBreeds()
                        }
                    }

                    LaunchedEffect(state.error) {
                        if (state.error) {
                            snackbarHostState.showSnackbar(
                                message = state.errorMessage,
                                duration = if (state.breeds.isEmpty()) {
                                    SnackbarDuration.Indefinite
                                } else {
                                    SnackbarDuration.Long
                                },
                            )
                        }
                    }

                    LaunchedEffect(state.loading) {
                        if (state.loading) {
                            snackbarHostState.showSnackbar(
                                message = "Loading...",
                                duration = SnackbarDuration.Indefinite,
                            )
                        } else {
                            snackbarHostState.currentSnackbarData?.dismiss()
                        }
                    }

                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(hostState = snackbarHostState)
                        },
                    ) { paddingValues ->
                        BreedListScreen(
                            state = listState,
                            breeds = state.breeds,
                            onClick = { breedId ->
                                navigation.push(configuration = Configuration.Detail(id = breedId))
                            },
                            onFavorite = { breedId, favorite ->
                                if (favorite) {
                                    component.setAsFavorite(breedId)
                                } else {
                                    component.unsetAsFavorite(breedId)
                                }
                            },
                            onSearchClick = {
                                navigation.push(configuration = Configuration.Search)
                            },
                            filterFavorites = component.filterFavorites,
                            onFilterFavoritesClick = {
                                component.filterFavorites = !component.filterFavorites
                            },
                            paddingValues = paddingValues,
                        )
                    }
                }

                Configuration.Search -> {
                    val component = remember { compositionRoot.breedSearchComponent }

                    val state by component.uiState.collectAsState()

                    val listState = rememberLazyStaggeredGridState()

                    val snackbarHostState = remember { SnackbarHostState() }

                    LaunchedEffect(state.error) {
                        if (state.error) {
                            snackbarHostState.showSnackbar(
                                message = state.errorMessage,
                                duration = SnackbarDuration.Long,
                            )
                        }
                    }

                    LaunchedEffect(state.loading) {
                        if (state.loading) {
                            snackbarHostState.showSnackbar(
                                message = "Loading...",
                                duration = SnackbarDuration.Indefinite,
                            )
                        } else {
                            snackbarHostState.currentSnackbarData?.dismiss()
                        }
                    }

                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(hostState = snackbarHostState)
                        },
                    ) { paddingValues ->
                        BreedSearchScreen(
                            navigateUp = navigation::pop,
                            keyword = component.keyword,
                            onKeywordChange = component::updateKeyword,
                            state = listState,
                            breeds = state.breeds,
                            onClick = { breedId ->
                                navigation.push(configuration = Configuration.Detail(id = breedId))
                            },
                            onFavorite = { breedId, favorite ->
                                if (favorite) {
                                    component.unsetAsFavorite(breedId)
                                } else {
                                    component.setAsFavorite(breedId)
                                }
                            },
                            paddingValues = paddingValues,
                        )
                    }
                }
            }
        }
    }
}
