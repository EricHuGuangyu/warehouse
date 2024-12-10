package com.example.warehouse

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.warehouse.data.model.User
import com.example.warehouse.data.repository.MockUserRepository
import com.example.warehouse.data.utils.DataStoreUtils
import com.example.warehouse.viewmodel.MainViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var userRepository: MockUserRepository
    private lateinit var context: Context
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk()
        context = mockk(relaxed = true) // Mock context for DataStoreUtils
        viewModel = MainViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getUserId should fetch new user ID if DataStore is empty`() = testScope.runTest {
        val mockUser = User("QAT","Guest")
        coEvery  { DataStoreUtils.getUserId(context) } returns null
        coEvery { userRepository.fetchNewUserId() } returns mockUser
        coEvery  { DataStoreUtils.putUserId(context, mockUser.userID!!) } just Runs

        viewModel.getUserId(context)
        advanceUntilIdle()

        assertEquals(mockUser.userID, viewModel.userId.value)
        coVerify { userRepository.fetchNewUserId() }
        coVerify { DataStoreUtils.putUserId(context, mockUser.userID!!) }
    }

    @Test
    fun `getUserId should retrieve user ID from DataStore`() = testScope.runTest {
        val storedUserId = "Guest"
        coEvery { DataStoreUtils.getUserId(context) } returns storedUserId

        viewModel.getUserId(context)
        advanceUntilIdle()

        assertEquals(storedUserId, viewModel.userId.value)
        coVerify { DataStoreUtils.getUserId(context) }
        coVerify(exactly = 0) { userRepository.fetchNewUserId() } // Ensure repository is not called
    }

    @Test
    fun `getUserId should do nothing if userId is already loaded`() = testScope.runTest {
        viewModel._userId.postValue("Guest")

        viewModel.getUserId(context)
        advanceUntilIdle()

        assertEquals("Guest", viewModel.userId.value)
        coVerify(exactly = 0) { userRepository.fetchNewUserId() }
        coVerify(exactly = 0) { DataStoreUtils.getUserId(context) }
    }
}