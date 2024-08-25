package com.example.yandexfoodtechcart.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.yandexfoodtechcart.CartApp
import com.example.yandexfoodtechcart.common.AppDispatchers
import com.example.yandexfoodtechcart.data.CartInteractor
import com.example.yandexfoodtechcart.domain.model.CartItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class CartViewModel(
    private val cartInteractor: CartInteractor,
    private val dispatchers: AppDispatchers
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableStateFlow<CartScreenEffect?>(null)
    val uiEffect = _uiEffect.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val items = withContext(dispatchers.io) {
                cartInteractor.getCartItems()
            }
            _uiState.update { CartScreenUiState(cartItems = items, isLoading = false) }
        }
    }

    fun onEvent(event: CartScreenEvent) {
        when (event) {
            CartScreenEvent.OnCartNext -> onCartNextClick()
            CartScreenEvent.OnClearCart -> onClearCart()
            CartScreenEvent.OnGoBack -> onGoBack()
            is CartScreenEvent.OnDecrementCount -> onDecrementCount(event.item)
            is CartScreenEvent.OnIncrementCount -> onIncrementCount(event.item)
        }
    }

    private fun onGoBack() {
        _uiEffect.update { CartScreenEffect.OnBack }
    }

    private fun onIncrementCount(item: CartItem) {
        viewModelScope.safeLaunch {
            withContext(dispatchers.io) {
                val updatedItem = item.copy(count = item.count + 1)
                _uiState.update { it.copy(isLoading = true) }
                cartInteractor.updateCartItem(updatedItem)
                _uiState.update {
                    CartScreenUiState(
                        cartItems = cartInteractor.getCartItems(),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun onDecrementCount(item: CartItem) {
        viewModelScope.safeLaunch {
            withContext(dispatchers.io) {
                val updatedItem = item.copy(count = item.count - 1)
                _uiState.update { it.copy(isLoading = true) }
                cartInteractor.updateCartItem(updatedItem)
                _uiState.update {
                    CartScreenUiState(
                        cartItems = cartInteractor.getCartItems(),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun onCartNextClick() {
        viewModelScope.safeLaunch {
            withContext(dispatchers.io) {
                _uiState.update { it.copy(isLoading = true) }
                _uiState.update { CartScreenUiState(cartItems = emptyList(), isLoading = false) }
            }
        }
    }

    private fun onClearCart() {
        viewModelScope.safeLaunch {
            _uiState.update { it.copy(isLoading = true) }
            cartInteractor.deleteCart()
            _uiState.update { CartScreenUiState(cartItems = emptyList(), isLoading = false) }
        }
    }

    private fun CoroutineScope.safeLaunch(block: suspend () -> Unit) {
        launch {
            withContext(dispatchers.io) {
                try {
                    block.invoke()
                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        _uiEffect.update { CartScreenEffect.ShowErrorMessage(e.message.orEmpty()) }
                    }
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as CartApp
                return CartViewModel(application.cartInteractor, application.dispatchers) as T
            }
        }
    }
}

sealed interface CartScreenEvent {
    data class OnIncrementCount(val item: CartItem) : CartScreenEvent
    data class OnDecrementCount(val item: CartItem) : CartScreenEvent
    data object OnClearCart : CartScreenEvent
    data object OnCartNext : CartScreenEvent
    data object OnGoBack : CartScreenEvent
}

data class CartScreenUiState(
    val cartItems: List<CartItem> = emptyList(),
    val isLoading: Boolean = false
)

sealed interface CartScreenEffect {
    data object OnBack : CartScreenEffect
    data class ShowErrorMessage(val message: String) : CartScreenEffect
}
