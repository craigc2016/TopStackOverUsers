# TopStackOverUsers
This app fetches the top 20 users of stackoverflow and pares a list of models of StackOverFlowUser. The model contains the values of userId, displayName, profileImageUrl and reputation.
This is a networking model which is then mapped to the UserUiModel which contains 2 extra values of isFollowed and ImageBitmap which is what the screen uses to render the data. Once the models are mapped to the ui models it is stored in a userCache map of userId to UserUiModel. 

When the map is populated fully inside a supervisor scope a coroutine is launched for each of the 20 users to load the image from the url using a http connection. The result is decoded into a bitmap and is then converted to the more compose friendly version of ImageBitmap loaded by the Image composable. If an image fails to load the value is simply set to null and the UI will display a placeholder image.

Once all the data is loaded the user can choose to follow / unfollow a user this is stored loacally in data store inside a value of followed_userId which is then put into a set. Together with the userCache map and set is how the follow state is handled for the app.

If loaded the users an error occurs the app will fall into an error state and display a message with an icon and a retry option to the user. If the user chooses retry the userCache is checked for items if so build the UI off that if not fetch the users again using loadUsers.

The project follows Clean Architecture principles by encapsulating image loading within a use case. This use case is responsible for decoding the raw byte array returned from the network request, allowing the image loading logic to remain reusable and independent of the UI layer.

Due to the restriction on using third-party libraries, the project uses a manual dependency injection approach. An AppContainer is responsible for creating, configuring, and providing application-level dependencies.

Unit tests where created for the view model to test the success and error states of loading users and images. Fakes where created for repository, decoder and image service since there was a restriction on a mocking library testing the success state of image loading was not possible because of its dependacy on the Android system. A mock server was added through a test library to valid the api response valid and empty states.

# Steps to install
- If you have access to GitHub you can fork the project and install and run through Android studio.
- On request I can email a zip of the project which can be ran from Android studio.
- On request can provide an APK file install on a device.

# Architecture
- **UI Layer (Jetpack Compose)**
  - Contains composable functions responsible for rendering the UI
  - Observes immutable UI state from the ViewModel
  - Emits user events to the ViewModel

- **ViewModel**
  - Holds UI state
  - Handles user actions and business logic
  - Exposes state via `StateFlow<UiState>`

- **Repository**
  - Acts as a single source of truth for data
  - Determines whether data should be fetched from remote APIs or local storage

- **Data Layer**
  - Remote data sources (REST APIs)
  - Local data sources (DataStore)

This separation ensures scalability, testability, and clean code.

- **UiState**
  - The Home screen uses a sealed class to represent all possible UI states:
    - **Loading** – Data is being fetched or processed
    - **Success** – Data is successfully loaded and ready to be displayed
    - **Error** – An error occurred while loading data
    - **Empty** *(optional)* – No data available to display
  - Using a sealed class ensures exhaustive `when` handling in Compose and prevents invalid UI states


# Libraries
- **Kotlin**
- **Jetpack Compose**
- **Android Jetpack**
  - ViewModel
  - Navigation Compose
- **Kotlin Coroutines & Flow** - Asynchronous programming and reactive streams
- **Retrofit** - Networking
  - Mock Server to unit test response
- **Kotlin Serialization 3** - For serializing JSON responses into data models
- **OkHttp** - HTTP client
- **Material 3** - UI components and theming
