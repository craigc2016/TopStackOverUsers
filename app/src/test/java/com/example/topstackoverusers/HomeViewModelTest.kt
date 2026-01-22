package com.example.topstackoverusers

import com.example.topstackoverusers.domain.LoadImageUseCase
import com.example.topstackoverusers.mocks.FakeImageDecoder
import com.example.topstackoverusers.mocks.FakeImageService
import com.example.topstackoverusers.mocks.FakeStackOverFlowRepository
import com.example.topstackoverusers.viewmodel.HomeViewModel
import com.example.topstackoverusers.viewmodel.UiState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()
    private lateinit var fakeImageService: FakeImageService
    private lateinit var fakeImageDecoder: FakeImageDecoder
    private lateinit var repository: FakeStackOverFlowRepository
    private lateinit var useCase: LoadImageUseCase

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        repository = FakeStackOverFlowRepository()
        fakeImageService = FakeImageService()
        fakeImageDecoder = FakeImageDecoder()
        useCase = LoadImageUseCase(repository, fakeImageDecoder)

    }

    @Test
    fun `load users changes to success state`() = runTest {
        val viewModel = createViewModel()
        val state = viewModel.uiState.first { it is UiState.Success }
        assertTrue(state is UiState.Success)
    }

    @Test
    fun `load users changes to success state with correct data`() = runTest {
        val viewModel = createViewModel()

        // When
        viewModel.loadUsers()

        // Allow coroutines to run
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.first { it is UiState.Success }
        assert(uiState is UiState.Success)

        val users = (uiState as UiState.Success).data

        assertEquals(3, users.size)

        // Verify mapping
        val user1 = users.first { it.userId == 1 }
        assertEquals("John Doe", user1.displayName)
        assertEquals(100, user1.reputation)

        // Verify followed state from fake repo
        assertTrue(user1.isFollowed)

        val user2 = users.first { it.userId == 2 }
        assertFalse(user2.isFollowed)
    }

    @Test
    fun `load users and have bad data erorr` () = runTest {
        // set up view model
        val viewModel = createViewModel(
            repository = FakeStackOverFlowRepository(throwOnGetUsers = true)
        )

        viewModel.loadUsers()

        assertTrue(viewModel.uiState.value is UiState.Loading)

        val state = viewModel.uiState.first { it is UiState.Error }
        assertTrue(state is UiState.Error)

    }

    @Test
    fun `load user and have no internet error retry` () = runTest {
        // Create local version will need to change throwOnGetUsers to get data
        val fakeRepo = FakeStackOverFlowRepository(throwOnGetUsers = true)
        // set up view model
        val viewModel = createViewModel(
            repository = fakeRepo
        )

        val collectJob = launch {
            viewModel.uiState.collect()
        }

        // First load → Error
        viewModel.loadUsers()
        advanceUntilIdle()


        val errorState = viewModel.uiState.first { it is UiState.Error }
        assertTrue(errorState is UiState.Error)

        //change repo to not throw exception for test
        fakeRepo.throwOnGetUsers = false

        // Retry
        viewModel.onRetry()

        // Allow coroutines to run
        advanceUntilIdle()

        val successState = viewModel.uiState.first { it is UiState.Success }
        assertTrue(successState is UiState.Success)

        val users = (successState as UiState.Success).data
        assertEquals(3, users.size)

        // Clean up
        collectJob.cancel()
    }


    @Test
    fun `load users and have profile image fail to load for user` () = runTest {
        val viewModel = createViewModel()

        // Trigger loading
        viewModel.loadUsers()

        // check state is loading
        assertTrue(viewModel.uiState.value is UiState.Loading)

        val state = viewModel.uiState.first { it is UiState.Success }
        assertTrue(state is UiState.Success)

        val firstUser = (state as UiState.Success).data.first()
        assertEquals("John Doe", firstUser.displayName)
        assertNull(firstUser.profileImage)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `follow user updates ui state`() = runTest {
        val fakeRepo = FakeStackOverFlowRepository(initialFollowed = emptySet())
        val viewModel = createViewModel(
            repository = fakeRepo,
        )

        // Load users
        viewModel.loadUsers()
        advanceUntilIdle()

        // User initially not followed
        var success = viewModel.uiState.first { it is UiState.Success } as UiState.Success
        val user2 = success.data.first { it.userId == 2 }
        assertFalse(user2.isFollowed)

        // Follow user
        viewModel.followUser(userId = 2, isFollowed = true)
        advanceUntilIdle()

        // Verify updated state
        success = viewModel.uiState.value as UiState.Success
        val updatedUser2 = success.data.first { it.userId == 2 }
        assertTrue(updatedUser2.isFollowed)
    }

    @Test
    fun ` unfollow user updates ui state`() = runTest {
        val fakeRepo = FakeStackOverFlowRepository()
        val viewModel = createViewModel(repository = fakeRepo)

        // Load users
        viewModel.loadUsers()
        advanceUntilIdle()

        // Get users followed
        var success = viewModel.uiState.first { it is UiState.Success } as UiState.Success
        val user1 = success.data.first { it.userId == 1 }
        //check user is followed
        assertTrue(user1.isFollowed)

        // Unfollow user
        viewModel.followUser(userId = 1, isFollowed = false)

        advanceUntilIdle()
        success = viewModel.uiState.value as UiState.Success
        val updatedUser2 = success.data.first { it.userId == 2 }
        assertTrue(!updatedUser2.isFollowed)
    }



    private fun createViewModel(
        repository: FakeStackOverFlowRepository = this.repository,
        decoder: FakeImageDecoder = this.fakeImageDecoder
    ) = HomeViewModel(
        repository = repository,
        loadImageUseCase = LoadImageUseCase(repository, decoder),
        ioDispatcher = mainDispatcherRule.dispatcher
    )

}