package com.example.warehouse

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.warehouse.data.model.PriceInfo
import com.example.warehouse.data.model.Product
import com.example.warehouse.data.remote.ApiService
import com.example.warehouse.data.utils.DataStoreUtils
import com.example.warehouse.viewmodel.ProductDetailViewModel
import com.example.warehouse.viewmodel.common.NetworkResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
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
class ProductDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var apiService: ApiService
    private lateinit var context: Context
    private lateinit var viewModel: ProductDetailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        apiService = mockk()
        context = mockk(relaxed = true) // Mock context for DataStoreUtils
        viewModel = ProductDetailViewModel(context, apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getProductDetails should update uiState with success on valid response`() = testScope.runTest {
        val mockBarCode = "123456"
        val mockUserId = "G"
        val mockProductDetail = Product(
            productName = "" +
                    "Guess How Much I Love You Board Book by Sam McBratney",
            productImageUrl = "https://okapi.office-supplies.co.nz/dw/image/v2/BDMG_DEV/on/" +
                    "demandware.static/-/Sites-twl-master-catalog/default/dw213b3b73/images/hi-res/BD/6D/R2001764_00.jpg",
            priceInfo = PriceInfo(price = 17.0f),
            productBarcode = "123456"
        )

        coEvery { DataStoreUtils.getUserId(context) } returns mockUserId
        coEvery { apiService.getProductDetail(any()) } returns mockProductDetail

        viewModel.getProductDetails(mockBarCode)
        advanceUntilIdle()

        assertEquals(NetworkResult.Success(mockProductDetail), viewModel.uiState.value)
        coVerify { DataStoreUtils.getUserId(context) }
        coVerify { apiService.getProductDetail(match { it["BarCode"] == mockBarCode }) }
    }

    @Test
    fun `getProductDetails should update uiState with error on exception`() = testScope.runTest {
        val mockBarCode = "123456"
        val mockUserId = "user_123"
        val exceptionMessage = "Network error"

        coEvery { DataStoreUtils.getUserId(context) } returns mockUserId
        coEvery { apiService.getProductDetail(any()) } throws Exception(exceptionMessage)

        viewModel.getProductDetails(mockBarCode)
        advanceUntilIdle()

        assert(viewModel.uiState.value is NetworkResult.Error)
        assertEquals("Network error", (viewModel.uiState.value as NetworkResult.Error).message)
        coVerify { DataStoreUtils.getUserId(context) }
        coVerify { apiService.getProductDetail(any()) }
    }

    @Test
    fun `getProductDetails should update uiState with loading before making API call`() = testScope.runTest {
        val mockBarCode = "123456"

        coEvery { DataStoreUtils.getUserId(context) } returns "user_123"
        coEvery { apiService.getProductDetail(any()) } returns Product(
                productName = "" +
                        "Guess How Much I Love You Board Book by Sam McBratney",
        productImageUrl = "https://okapi.office-supplies.co.nz/dw/image/v2/BDMG_DEV/on/" +
                "demandware.static/-/Sites-twl-master-catalog/default/dw213b3b73/images/hi-res/BD/6D/R2001764_00.jpg",
        priceInfo = PriceInfo(price = 17.0f)
        )

        viewModel.getProductDetails(mockBarCode)

        // Verify Loading state is set first
        assertEquals(NetworkResult.Loading, viewModel.uiState.value)
    }
}
