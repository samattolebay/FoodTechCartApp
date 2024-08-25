package com.example.yandexfoodtechcart.domain.model

data class CartItem(
    val id: String,
    val name: String,
    val count: Int,
    val price: Int,
    val currency: String,
    val imageUrl: String,
)
