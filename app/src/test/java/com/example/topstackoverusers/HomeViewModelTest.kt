package com.example.topstackoverusers

import com.example.topstackoverusers.viewmodel.HomeViewModel
import com.example.topstackoverusers.viewmodel.UiState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


class HomeViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var repository: FakeStackOverFlowRepository
    private lateinit var viewModel: HomeViewModel


    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        // Initialize the TestDispatcher before each test
        testDispatcher = StandardTestDispatcher()
        // Set the Main dispatcher to use our test dispatcher
        Dispatchers.setMain(testDispatcher)
        repository = FakeStackOverFlowRepository()
        viewModel = HomeViewModel(repository, ioDispatcher = testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        // Reset the Main dispatcher after each test to avoid interference
        Dispatchers.resetMain()
    }

    @Test
    fun `load users changes to success state`() = runTest {
        val state = viewModel.uiState.first { it is UiState.Success }
        assertTrue(state is UiState.Success)
    }

    @Test
    fun `load users changes to success state with correct data`() = runTest {
        // When
        viewModel.loadUsers()

        // Allow coroutines to run
        testDispatcher.scheduler.advanceUntilIdle()

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
    fun `image load updates user in cache`() = runTest {
        viewModel.loadUsers()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first {
            it is UiState.Success &&
                    it.data.first().profileImage != null
        }

        val user = (state as UiState.Success).data.first()
        assertNotNull(user.profileImage)
    }


}