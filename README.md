# 🎬 Benedict Movies - Benedict Cumberbatch Movie App

A modern Android application showcasing Benedict Cumberbatch's filmography, built with Jetpack Compose and following Clean Architecture principles.

## 📱 Features

- **Movie List**: Browse Benedict Cumberbatch's complete filmography with grid/list view toggle
- **Movie Details**: Detailed information including ratings, genres, budget, revenue, and runtime
- **Similar Movies**: Discover similar movies to the one selected, with recursive navigation
- **Retry Mechanism**: User-friendly error handling with retry buttons on both screens
- **Modern UI**: Built with Jetpack Compose and Material Design 3
- **Error Handling**: Robust error handling with clear user feedback and retry options
- **Dark/Light Theme**: Full theme support with system preference detection
- **Clean Architecture**: Separation of concerns with domain, data, and presentation layers

## 🚀 Setup Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or higher
- Android SDK with minimum API 24 (Android 7.0)
- TMDB API Key (free registration required)

### Installation Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/benedict-movies.git
   cd benedict-movies
   ```

2. **Get TMDB API Key:**
   - Register for a free account at [The Movie Database](https://www.themoviedb.org/)
   - Navigate to Settings → API → Request API Key
   - Choose "Developer" option and fill in the required information
   - Copy your API v3 key

3. **Configure API Key:**
   
   **Option 1: Direct Configuration (for testing)**
   - Open `app/build.gradle.kts`
   - Replace the placeholder API key in the `buildConfigField` with your actual key:
   ```kotlin
   buildConfigField("String", "TMDB_API_KEY", "\"your_actual_api_key_here\"")
   ```

   **Option 2: Environment Variable (recommended for production)**
   - Add to your `local.properties` file:
   ```properties
   TMDB_API_KEY=your_actual_api_key_here
   ```
   - Update `build.gradle.kts` to read from properties

4. **Sync and Build:**
   ```bash
   ./gradlew clean build
   ```

5. **Run the App:**
   - Connect an Android device or start an emulator
   - Run the app from Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run unit tests with coverage report
./gradlew testDebugUnitTest jacocoTestReport

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests "MovieViewModelTest"
```

## 🏗️ Architecture Decisions & Reasoning

### Clean Architecture

**Decision:** Implemented Clean Architecture with three distinct layers: Presentation, Domain, and Data.

**Reasoning:**
- **Separation of Concerns**: Each layer has a single responsibility, making the codebase easier to understand and maintain
- **Testability**: Business logic in the domain layer can be tested independently of Android framework
- **Flexibility**: Easy to swap implementations (e.g., replace network data source with local database) without affecting other layers
- **Scalability**: New features can be added without impacting existing code
- **Team Collaboration**: Clear boundaries make it easier for multiple developers to work simultaneously

**Implementation:**
- **Domain Layer**: Pure Kotlin with no Android dependencies - contains business logic, use cases, and domain models
- **Data Layer**: Handles data operations, API calls, and data mapping from DTOs to domain models
- **Presentation Layer**: Android-specific UI code using Jetpack Compose and ViewModels

### MVVM Pattern

**Decision:** Used MVVM (Model-View-ViewModel) pattern for the presentation layer.

**Reasoning:**
- **Lifecycle Awareness**: ViewModels survive configuration changes, preventing data loss
- **Reactive UI**: StateFlow enables reactive UI updates with minimal boilerplate
- **Testability**: ViewModels can be unit tested without Android framework dependencies
- **Clear Separation**: Business logic is separated from UI rendering logic
- **Jetpack Compose Integration**: Works seamlessly with Compose's reactive nature

### Use Case Pattern

**Decision:** Implemented individual use cases for each business operation (GetMoviesUseCase, GetMovieDetailUseCase, GetSimilarMoviesUseCase).

**Reasoning:**
- **Single Responsibility**: Each use case handles one specific business operation
- **Reusability**: Use cases can be composed and reused across different ViewModels
- **Testability**: Easy to test business logic in isolation
- **Maintainability**: Changes to business rules are localized to specific use cases
- **Clear Intent**: Use case names clearly communicate what operation they perform

### Dependency Injection with Hilt

**Decision:** Used Hilt for dependency injection instead of manual DI or other frameworks.

**Reasoning:**
- **Compile-Time Validation**: Catches DI errors at compile time, not runtime
- **Android Integration**: Built specifically for Android with lifecycle awareness
- **Boilerplate Reduction**: Less code compared to manual DI or Dagger
- **Scoping**: Automatic lifecycle scoping (ActivityScoped, ViewModelScoped, etc.)
- **Testing Support**: Easy to replace dependencies with test doubles

## 📚 Libraries Used & Why

### UI Layer

**Jetpack Compose**
- **Why**: Modern declarative UI with less boilerplate than XML
- **Benefits**: Reactive updates, type-safe, excellent preview support, better performance
- **Use Case**: All Compose screens (MovieDetailScreen, SimilarMoviesSection)

**Material Design 3**
- **Why**: Latest design system with dynamic theming support
- **Benefits**: Consistent UI, accessibility built-in, adaptive layouts
- **Use Case**: Color schemes, typography, component styling

**Coil**
- **Why**: Kotlin-first image loading library designed for Compose
- **Benefits**: Coroutine support, automatic memory/disk caching, small size (~300KB)
- **Alternative Considered**: Glide (chose Coil for better Compose integration)

### Navigation

**Navigation Compose**
- **Why**: Type-safe navigation specifically designed for Compose
- **Benefits**: Deep linking, argument passing, back stack management
- **Use Case**: Navigation between movie list and detail screens

### Networking

**Retrofit**
- **Why**: Industry standard, battle-tested, excellent documentation
- **Benefits**: Type-safe API calls, coroutine support, easy error handling
- **Alternative Considered**: Ktor (chose Retrofit for maturity and community support)

**OkHttp**
- **Why**: Required by Retrofit, provides advanced HTTP features
- **Benefits**: Connection pooling, interceptors for logging, caching support
- **Use Case**: Network layer communication, API request logging

**Gson**
- **Why**: Reliable JSON serialization/deserialization
- **Benefits**: No reflection at runtime, mature library, well-tested
- **Alternative Considered**: Moshi (chose Gson for simplicity)

### Dependency Injection

**Hilt**
- **Why**: Official Android DI solution built on Dagger
- **Benefits**: Less boilerplate than Dagger, Android lifecycle awareness
- **Alternative Considered**: Koin (chose Hilt for compile-time safety)

### State Management

**Kotlin Coroutines + Flow**
- **Why**: Native Kotlin async solution, better than RxJava for this use case
- **Benefits**: Structured concurrency, cancellation support, easy testing
- **Use Case**: Async operations, state management, data streams

**StateFlow**
- **Why**: Hot stream that holds and emits state updates
- **Benefits**: Lifecycle-aware, replays last value to new collectors
- **Use Case**: ViewModel state management, UI state updates

### Testing

**JUnit 4**
- **Why**: Standard testing framework for unit tests
- **Use Case**: All unit tests

**MockK**
- **Why**: Kotlin-first mocking library with better DSL than Mockito
- **Benefits**: Suspend function support, relaxed mocks, clear syntax
- **Alternative Considered**: Mockito (chose MockK for Kotlin features)

**Truth**
- **Why**: Fluent assertion library from Google
- **Benefits**: More readable assertions, better error messages
- **Alternative Considered**: AssertJ (chose Truth for Android integration)

**Coroutines Test**
- **Why**: Official testing utilities for coroutines
- **Benefits**: TestDispatcher, runTest, virtual time control
- **Use Case**: Testing async operations in ViewModels and use cases

## 🔧 What Would Be Improved With More Time

### 1. **Pagination Implementation**
- **Current**: Loads all movies at once
- **Improvement**: Implement paging with Paging 3 library for efficient data loading
- **Benefit**: Better performance with large datasets, reduced memory usage

### 2. **Offline Support with Room Database**
- **Current**: Network-only data source
- **Improvement**: Cache movie data in Room database for offline access
- **Benefit**: App works without internet, faster initial load times

### 3. **Advanced Error Handling**
- **Current**: Basic error messages
- **Improvement**: 
  - Categorized errors (network, server, timeout, etc.)
  - Exponential backoff for retries
  - Error analytics integration
- **Benefit**: Better user experience, easier debugging

### 4. **Search & Filter Functionality**
- **Improvement**: Add search by movie title, filter by genre, year, rating
- **Benefit**: Better discoverability, improved user experience

### 5. **UI/UX Enhancements**
- **Improvements**:
  - Skeleton loading screens instead of progress indicators
  - Smooth animations between screens
  - Image placeholder states
  - Pull-to-refresh with animation
- **Benefit**: More polished, professional feel

### 6. **Performance Optimizations**
- **Improvements**:
  - Image size optimization based on device density
  - Lazy loading for similar movies section
  - Compose stability optimizations
  - Memory leak detection with LeakCanary
- **Benefit**: Smoother performance, better battery life

### 7. **Accessibility Improvements**
- **Improvements**:
  - Better content descriptions for images
  - TalkBack testing and optimization
  - High contrast mode support
  - Font scaling support
- **Benefit**: Inclusive design, larger user base

### 8. **CI/CD Pipeline**
- **Improvements**:
  - GitHub Actions for automated testing
  - Automated code quality checks (ktlint, detekt)
  - Automated releases with semantic versioning
- **Benefit**: Faster development cycle, fewer bugs in production

### 9. **Analytics & Monitoring**
- **Improvements**:
  - Firebase Crashlytics for crash reporting
  - Firebase Analytics for user behavior tracking
  - Performance monitoring
- **Benefit**: Data-driven decisions, proactive bug fixing

### 10. **Feature Additions**
- Favorites/Watchlist functionality
- Share movie details
- Movie trailers integration
- Actor information pages
- User reviews and ratings

## 🚧 Challenges Encountered & Solutions

### 1. **Challenge: Hilt ViewModel Injection in Tests**
**Problem**: Initial UI test setup tried to directly inject `@HiltViewModel` which is prohibited by Hilt's design.

**Solution**: 
- Removed direct ViewModel injection in tests
- Used ViewModelProvider API as recommended
- Mocked use cases instead of injecting ViewModel directly
- Created proper test structure with test doubles

**Learning**: Understanding Hilt's restrictions around ViewModel injection improved test architecture.

### 2. **Challenge: Retry Button Not Actually Retrying**
**Problem**: Retry button in MovieDetailScreen was only clearing errors, not reloading data.

**Solution**:
- Added `movieId` parameter to `MovieDetailScreen`
- Updated retry button to call both `clearError()` and `loadMovieDetail(movieId)`
- Added similar functionality to MovieListFragment

**Learning**: Importance of verifying that UI actions actually trigger the intended business logic.

### 3. **Challenge: Similar Movies Loading Timing**
**Problem**: Deciding when to load similar movies - on detail load or user action.

**Solution**:
- Implemented automatic loading after movie details successfully load
- Added separate loading state for similar movies
- Provided error handling specific to similar movies

**Learning**: Automatic loading improves UX by reducing user actions, but requires careful state management.

### 4. **Challenge: Testing Async Operations**
**Problem**: Initial tests were flaky due to improper coroutine test setup.

**Solution**:
- Properly configured `TestDispatcher` and `TestScope`
- Used `advanceUntilIdle()` to wait for coroutines to complete
- Set up test coroutine dispatcher with `Dispatchers.setMain()`

**Learning**: Coroutine testing requires understanding of test dispatchers and virtual time.

### 5. **Challenge: State Management Complexity**
**Problem**: Managing multiple loading/error states (movies, details, similar) became complex.

**Solution**:
- Created comprehensive `MovieUiState` data class
- Separated concerns with distinct state properties
- Implemented clear state transition logic in ViewModel

**Learning**: A well-designed state model simplifies UI logic significantly.

### 6. **Challenge: MockK "No Answer Found" Errors**
**Problem**: Tests failing because mocks weren't set up for automatically triggered operations.

**Solution**:
- Identified that `loadMovieDetail` automatically calls `loadSimilarMovies`
- Added missing mock setups for transitive operations
- Created comprehensive test coverage for all paths

**Learning**: Understanding the full call chain is crucial for effective mocking.

## 🏗️ Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/rushdroid/benedictmovies/
│   │   │   ├── core/                     # Core utilities and constants
│   │   │   │   ├── constants/            # App-wide constants (Person IDs)
│   │   │   │   └── util/                 # Utility classes (formatters, helpers)
│   │   │   ├── data/                     # Data layer
│   │   │   │   ├── remote/
│   │   │   │   │   ├── api/              # Retrofit API interfaces
│   │   │   │   │   └── dto/              # Data Transfer Objects (JSON models)
│   │   │   │   ├── mapper/               # DTO to Domain model mapping
│   │   │   │   └── repository/           # Repository implementations
│   │   │   ├── di/                       # Hilt DI modules
│   │   │   ├── domain/                   # Business logic layer (Pure Kotlin)
│   │   │   │   ├── model/                # Domain entities
│   │   │   │   ├── repository/           # Repository interfaces
│   │   │   │   └── usecase/              # Business use cases
│   │   │   └── presentation/             # UI layer
│   │   │       ├── theme/                # Material Design 3 theming
│   │   │       ├── ui/
│   │   │       │   ├── activity/         # Activities (entry points)
│   │   │       │   ├── adapter/          # RecyclerView adapters
│   │   │       │   ├── compose/          # Jetpack Compose screens
│   │   │       │   └── fragment/         # XML Fragments (legacy UI)
│   │   │       └── viewmodel/            # ViewModels (state management)
│   │   └── res/                          # Android resources
│   ├── test/                            # Unit tests (business logic)
│   │   ├── data/repository/             # Repository tests
│   │   ├── domain/usecase/              # Use case tests
│   │   └── presentation/viewmodel/      # ViewModel tests
│   └── androidTest/                     # Instrumented tests (UI tests)
└── build.gradle.kts                     # Module dependencies
```

## 🧪 Testing Strategy

### Unit Tests
Comprehensive unit tests covering business logic across all layers:

**Test Coverage Areas:**
- **ViewModel Tests**: State management and business logic validation
- **Use Case Tests**: Domain logic and business rules
- **Repository Tests**: Data layer operations and error handling
- **Mapper Tests**: Data transformation accuracy between layers

**Testing Libraries:**
- **MockK**: Kotlin-friendly mocking framework for creating test doubles
- **Truth**: Fluent assertion library for readable test code
- **Coroutines Test**: Testing utilities for asynchronous code
- **Arch Core Testing**: LiveData and ViewModel testing utilities

**Testing Best Practices:**
- AAA Pattern (Arrange, Act, Assert) for clear test structure
- Test data builders for reusable and maintainable test fixtures
- Comprehensive edge case and error scenario coverage
- Isolated unit tests with mocked dependencies

### UI Tests
Jetpack Compose UI tests for user interaction validation:

**UI Test Coverage:**
- Screen state transitions (loading, error, content)
- User interactions (clicks, scrolls, navigation)
- Theme switching (dark/light mode)
- Accessibility compliance

**UI Testing Libraries:**
- **Compose UI Test**: Testing utilities for Compose components
- **Espresso**: Android UI testing framework for reliable interactions

## 🎨 UI/UX Design Principles

### Material Design 3
- **Dynamic Theming**: System theme preference adaptation
- **Color Schemes**: Semantic color usage for consistency
- **Typography**: Material Design typography scale
- **Elevation**: Proper surface elevation hierarchy
- **Motion**: Smooth transitions and animations

### Jetpack Compose Best Practices
- **Stateless Composables**: UI components without internal state for better reusability
- **State Hoisting**: State managed at appropriate levels in component hierarchy
- **Recomposition Optimization**: Efficient UI updates minimizing unnecessary redraws
- **Preview Support**: Comprehensive preview functions for rapid development
- **Accessibility**: Support for screen readers and accessibility services

## 🔧 Build Configuration

### Gradle Setup
- **Version Catalogs**: Centralized dependency management for consistency
- **KSP**: Kotlin Symbol Processing for faster annotation processing
- **Build Variants**: Debug and Release configurations with different settings
- **ProGuard**: Code obfuscation and optimization for release builds

## 📊 Performance Optimizations

### Network Optimizations
- **HTTP Caching**: OkHttp response caching for reduced network calls
- **Image Caching**: Multi-level caching (memory + disk) via Coil
- **Connection Pooling**: Efficient network resource reuse

### UI Performance
- **LazyColumn/LazyRow**: Efficient list rendering with view recycling
- **Stable Classes**: Optimized recomposition through stability annotations
- **State Management**: Minimal state updates to reduce recomposition

### Memory Management
- **Lifecycle Awareness**: Automatic resource cleanup on lifecycle events
- **Coroutine Scoping**: Proper coroutine cancellation on ViewModel clear
- **Image Optimization**: Automatic bitmap pooling and size reduction

## 🔒 Security Considerations

### API Security
- **HTTPS Only**: Secure communication with TMDB API
- **API Key Protection**: Build-time configuration preventing exposure
- **Network Security Config**: Android network security configuration

### Data Protection
- **Input Validation**: Sanitized user inputs preventing injection attacks
- **Error Handling**: User-friendly error messages without sensitive details
- **Logging**: Production-safe logging excluding sensitive information

## 🤝 Contributing

### Development Workflow
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Standards
- **Kotlin Coding Conventions**: Follow official Kotlin style guide
- **Compose Guidelines**: Jetpack Compose best practices
- **Test Coverage**: Minimum 80% code coverage for new features
- **Documentation**: Comprehensive KDoc comments for public APIs
- **Code Review**: All PRs require at least one approval

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **TMDB API**: Movie data provided by The Movie Database
- **Benedict Cumberbatch**: For his amazing filmography
- **Android Community**: For excellent libraries and resources
- **Material Design**: For comprehensive design system
- **JetBrains**: For the Kotlin programming language

## 📞 Contact & Support

- **Developer**: Rushabh Prajapati
- **GitHub**: [@rushabhprajapati](https://github.com/rushabhprajapati)
- **Issues**: [GitHub Issues](https://github.com/yourusername/benedict-movies/issues)

---

**Built with ❤️ using Modern Android Development practices**

*This project demonstrates production-ready Android development with Clean Architecture, MVVM, Jetpack Compose, and comprehensive testing.*
