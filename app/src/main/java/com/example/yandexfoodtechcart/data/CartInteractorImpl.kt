package com.example.yandexfoodtechcart.data

import com.example.yandexfoodtechcart.domain.model.CartItem
import kotlinx.coroutines.delay

class CartInteractorImpl : CartInteractor {

    private var cartItems = mutableMapOf<String, CartItem>().apply {
        putAll(initialCartItems.map { it.id to it })
    }

    override suspend fun getCartItems(): List<CartItem> {
        delay(1000)
        return cartItems.values.toList()
    }

    override suspend fun deleteCart() {
        delay(1000)
        cartItems.clear()
    }

    override suspend fun updateCartItem(item: CartItem) {
        delay(500)
        cartItems[item.id] = item
    }

    override suspend fun updateCartItem(items: List<CartItem>) {
        items.forEach { updateCartItem(it) }
    }
}

val initialCartItems = listOf(
    CartItem(
        id = "1",
        name = "Суп Том Ям с морепродуктами",
        count = 1,
        price = 100,
        currency = "₽",
        imageUrl = "https://storage-seafood-shop.storage.yandexcloud.net/iblock/8aa/8aae18b3742032aabcc7f6c8ecc5e22e/82ccf1e57ac0a3acd3db365bf0c83dca.jpg"
    ),
    CartItem(
        id = "2",
        name = "Поке с индейкой и чуккой",
        count = 1,
        price = 100,
        currency = "₽",
        imageUrl = "https://cdn.food.ru/unsigned/fit/640/480/ce/0/czM6Ly9tZWRpYS9waWN0dXJlcy9yZWNpcGVzLzEyNi9zdGVwcy80UjRKeVUuanBn.jpg"
    ),
    CartItem(
        id = "3",
        name = "Поке с тунцом, лососем, авокадо, чеснок и тд.Поке с тунцом, лососем, авокадо, чеснок и тд.",
        count = 1,
        price = 100,
        currency = "₽",
        imageUrl = "https://static.1000.menu/img/content-v2/ed/f6/69423/poke-boul_1668367268_17_max.jpg"
    ),
    CartItem(
        id = "4",
        name = "Блинчики с малиной маскарпоне",
        count = 1,
        price = 100,
        currency = "₽",
        imageUrl = "https://img.iamcook.ru/2024/upl/recipes/cat/u-4eeeff5e87ab13bdb7216268c476e592.jpg"
    )
)
