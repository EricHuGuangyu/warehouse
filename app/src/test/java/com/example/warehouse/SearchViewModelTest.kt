package com.example.warehouse

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.warehouse.data.model.PriceInfo
import com.example.warehouse.data.model.Product
import com.example.warehouse.data.model.SearchResult
import com.example.warehouse.data.model.SortOptions
import com.example.warehouse.data.remote.ApiService
import com.example.warehouse.data.utils.DataStoreUtils
import com.example.warehouse.viewmodel.SearchViewModel
import com.example.warehouse.viewmodel.common.NetworkResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
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
class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var apiService: ApiService
    private lateinit var context: Context
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        apiService = mockk()
        context = mockk(relaxed = true)
        viewModel = SearchViewModel(context, apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadSearchResults should update uiState with success for new search`() = testScope.runTest {
        val keyword = "laptop"
        val mockProducts = listOf(
            Product("1", "Product A",  priceInfo = PriceInfo(price = 167.0f)),
            Product("2", "Product B",  priceInfo = PriceInfo(price = 17.0f))
        )
        val mockResponse = searchResponse(mockProducts)

        coEvery { DataStoreUtils.getUserId(context) } returns "user_123"
        coEvery { apiService.getSearchResult(any()) } returns mockResponse

        viewModel.loadSearchResults(keyword, isLoadMore = false)
        advanceUntilIdle()

        assertEquals(NetworkResult.Success(mockProducts), viewModel.uiState.value)
        assertEquals(mockProducts, viewModel.cachedResults.value)
        coVerify { apiService.getSearchResult(any()) }
    }

    @Test
    fun `loadSearchResults should append results for load more`() = testScope.runTest {
        val keyword = "laptop"
        val initialProducts = listOf(Product("1", "Product A",  priceInfo = PriceInfo(price = 17.0f)))
        val newProducts = listOf(Product("2", "Product B",  priceInfo = PriceInfo(price = 17.0f)))
        val mockInitialResponse = searchResponse(initialProducts)
        val mockNewResponse = searchResponse(newProducts)

        coEvery { DataStoreUtils.getUserId(context) } returns "user_123"
        coEvery { apiService.getSearchResult(any()) } returnsMany listOf(mockInitialResponse, mockNewResponse)

        // Initial search
        viewModel.loadSearchResults(keyword, isLoadMore = false)
        advanceUntilIdle()
        assertEquals(NetworkResult.Success(initialProducts), viewModel.uiState.value)
        assertEquals(initialProducts, viewModel.cachedResults.value)

        // Load more
        viewModel.loadSearchResults(keyword, isLoadMore = true)
        advanceUntilIdle()
        assertEquals(NetworkResult.Success(initialProducts + newProducts), viewModel.uiState.value)
        assertEquals(initialProducts + newProducts, viewModel.cachedResults.value)
        assertFalse(viewModel.isLoadingMore.value)
    }

    @Test
    fun `loadSearchResults should handle errors`() = testScope.runTest {
        val keyword = "laptop"
        val exceptionMessage = "Network error"

        coEvery { DataStoreUtils.getUserId(context) } returns "user_123"
        coEvery { apiService.getSearchResult(any()) } throws Exception(exceptionMessage)

        viewModel.loadSearchResults(keyword, isLoadMore = false)
        advanceUntilIdle()

        assert(viewModel.uiState.value is NetworkResult.Error)
        assertEquals("Network error", (viewModel.uiState.value as NetworkResult.Error).message)
        coVerify { apiService.getSearchResult(any()) }
    }

    @Test
    fun `loadSearchResults should set isLoadingMore when loading more`() = testScope.runTest {
        val keyword = "laptop"
        val mockProducts = listOf(Product(
            productName = "Product A" +
                    "Guess How Much I Love You Board Book by Sam McBratney",
            productImageUrl = "https://okapi.office-supplies.co.nz/dw/image/v2/BDMG_DEV/on/" +
                    "demandware.static/-/Sites-twl-master-catalog/default/dw213b3b73/images/hi-res/BD/6D/R2001764_00.jpg",
            priceInfo = PriceInfo(price = 17.0f)
        ),Product(
            productName = "Product B" +
                    "Guess How Much I Love You Board Book by Sam McBratney",
            productImageUrl = "https://okapi.office-supplies.co.nz/dw/image/v2/BDMG_DEV/on/" +
                    "demandware.static/-/Sites-twl-master-catalog/default/dw213b3b73/images/hi-res/BD/6D/R2001764_00.jpg",
            priceInfo = PriceInfo(price = 17.0f)
        ))


        val mockResponse = searchResponse(mockProducts)

        coEvery { DataStoreUtils.getUserId(context) } returns "user_123"
        coEvery { apiService.getSearchResult(any()) } returns mockResponse

        viewModel.loadSearchResults(keyword, isLoadMore = true)

        // Check loading state
        assertTrue(viewModel.isLoadingMore.value)
        advanceUntilIdle()

        // Verify loading state reset and results
        assertFalse(viewModel.isLoadingMore.value)
        assertEquals(NetworkResult.Success(mockProducts), viewModel.uiState.value)
    }

    private fun searchResponse(mockProducts:List<Product>): SearchResult{

        val mockSortOptions = listOf(
            SortOptions("price", "asc"),
            SortOptions("popularity", "desc")
        )


        return SearchResult(
            products = mockProducts,
            searchTerm = "laptop",
            suggestions = null,
            total = 2,
            facets = null,
            sortOptions = mockSortOptions,
            guest = true,
            platformDemandWare = "platform_123",
            environment = "production",
            developmentPlatform = false,
            apiVersion = 2.0,
            requestedApiVersion = 2.0
        )

    }
}
