package com.example.yandexfoodtechcart

import android.app.Application
import com.example.yandexfoodtechcart.common.AppDispatchers
import com.example.yandexfoodtechcart.data.CartInteractor
import com.example.yandexfoodtechcart.data.CartInteractorImpl

class CartApp : Application() {

    lateinit var cartInteractor: CartInteractor
    lateinit var dispatchers: AppDispatchers

    override fun onCreate() {
        super.onCreate()
        cartInteractor = CartInteractorImpl()
        dispatchers = AppDispatchers()
    }
}
