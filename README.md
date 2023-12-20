# Cat Breed Explorer

![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/horaciocome1/cat-breed-explorer-multiplatform/android-build.yml?label=build) ![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/horaciocome1/cat-breed-explorer-multiplatform/android-testing.yml?label=unit%20test) ![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/horaciocome1/cat-breed-explorer-multiplatform/android-lint.yml?label=lint) ![GitHub top language](https://img.shields.io/github/languages/top/horaciocome1/cat-breed-explorer-multiplatform) ![GitHub release (with filter)](https://img.shields.io/github/v/release/horaciocome1/cat-breed-explorer-multiplatform) ![GitHub repo size](https://img.shields.io/github/repo-size/horaciocome1/cat-breed-explorer-multiplatform) ![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/horaciocome1/cat-breed-explorer-multiplatform) ![GitHub commit activity (branch)](https://img.shields.io/github/commit-activity/w/horaciocome1/cat-breed-explorer-multiplatform)

CatBreedExplorer is a native Kotlin Multiplatform (Android and iOS) app that allows users to explore various cat breeds using TheCatAPI. The app provides a comprehensive list of cat breeds, including images, origins, and detailed information. Users can mark their favorite breeds, search for specific breeds, and even explore offline with locally stored data.

## Features

- **Breed List Screen:**
   - Display a list of cat breeds with images, names, and origins.
   - Additional information can be viewed for each breed.
   - Search bar to filter breeds by name.
   - Navigate to the Detail screen by selecting a breed.

- **Detail Screen:**
   - View detailed information for a selected breed, including image, name, origin, life span, temperament, and description.
   - Option to add/remove the breed from favorites.

- **Favorites Screen:**
   - List of favorite cat breeds.
   - Search bar to filter favorite breeds by name.
   - Navigate to the Detail screen by selecting a breed.

- **Offline Functionality:**
   - All data is cached locally, allowing the app to function offline.
   - Utilizes local storage to display information when there is no internet connection.

## Set up the environment

> **Warning**
> You need a Mac with macOS to write and run iOS-specific code on simulated or real devices.
> This is an Apple requirement.

To work with this template, you need the following:

* A machine running a recent version of macOS
* [Xcode](https://apps.apple.com/us/app/xcode/id497799835)
* [Android Studio](https://developer.android.com/studio)
* The [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
* The [CocoaPods dependency manager](https://kotlinlang.org/docs/native-cocoapods.html)

### Check your environment

Before you start, use the [KDoctor](https://github.com/Kotlin/kdoctor) tool to ensure that your development environment is configured correctly:

1. Install KDoctor with [Homebrew](https://brew.sh/):

    ```text
    brew install kdoctor
    ```

2. Run KDoctor in your terminal:

    ```text
    kdoctor
    ```

   If everything is set up correctly, you'll see valid output:

   ```text
   Environment diagnose (to see all details, use -v option):
   [✓] Operation System
   [✓] Java
   [✓] Android Studio
   [✓] Xcode
   [✓] Cocoapods
   
   Conclusion:
     ✓ Your system is ready for Kotlin Multiplatform Mobile development!
   ```

Otherwise, KDoctor will highlight which parts of your setup still need to be configured and will suggest a way to fix them.

## Usage

- Browse the list of cat breeds on the Breed List screen.
- Select a breed to view detailed information on the Detail screen.
- Add or remove breeds from favorites using the button provided.
- Explore your favorite breeds with the favorites filter on toolbar.
- Use the search bar to filter breeds by name on both the Breed List and Favorites screens.

## Technologies Used

- Native Android development (Kotlin)
- [TheCatAPI](https://thecatapi.com/) - for cat breed data
- [Ktor](https://ktor.io/docs/welcome.html) - a framework to easily build connected applications –
  web applications, HTTP services, mobile and browser applications
- [Kotlin Serialization](https://kotlinlang.org/docs/serialization.html) - provides sets of
  libraries for all supported platforms – JVM, JavaScript, Native – and for various serialization
  formats – JSON, CBOR, protocol buffers, and others
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-guide.html) - a rich library for
  coroutines developed by JetBrains
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - recommended modern toolkit for
  building native UI
- [Material 3](https://m3.material.io) - latest version of Google’s open-source design system
- [SQLDelight](https://cashapp.github.io/sqldelight/) - Generates typesafe Kotlin APIs from SQL
- [Kamel](https://github.com/Kamel-Media/Kamel) - Kotlin asynchronous media loading and caching library for Compose
- [Decompose](https://arkivanov.github.io/Decompose/) - Kotlin Multiplatform library for breaking down your code into lifecycle-aware business logic components (aka BLoC)
- [Timber](https://github.com/JakeWharton/timber) - a logger with a small, extensible API which provides utility on top of Android's normal `Log` class
- [App Startup](https://developer.android.com/topic/libraries/app-startup) - provides a straightforward, performant way to initialize components at application startup
- [Immutable Collections Library for Kotlin](https://github.com/Kotlin/kotlinx.collections.immutable) - Immutable collection interfaces and implementation prototypes for Kotlin

## Contribution

Contributions are welcome! Please follow these steps if you want to contribute to the project.

1. Fork the repository.
2. Create a new branch: `git checkout -b feature/new-feature`
3. Make your changes and commit them: `git commit -m 'Add new feature'`
4. Push to the branch: `git push origin feature/new-feature`
5. Submit a pull request.

## License

This project is licensed under the Apache License, Version 2.0 License - see the [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0) doc for details.

### External assets

- <a href="https://www.flaticon.com/free-icons/cat" title="cat icons">Cat icons created by Icon Mela - Flaticon</a>

---

**CatBreedExplorer** - Explore the world of cat breeds with this delightful app! 🐾

For more information, contact [Horácio Comé](https://horaciocome1.github.io/).
