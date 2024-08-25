package com.example.yandexfoodtechcart.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.yandexfoodtechcart.R
import com.example.yandexfoodtechcart.data.initialCartItems
import com.example.yandexfoodtechcart.domain.model.CartItem
import com.example.yandexfoodtechcart.ui.theme.BackgroundCounterColor
import com.example.yandexfoodtechcart.ui.theme.BackgroundDividerColor
import com.example.yandexfoodtechcart.ui.theme.BackgroundGrayColor
import com.example.yandexfoodtechcart.ui.theme.ButtonShape
import com.example.yandexfoodtechcart.ui.theme.CartBackgroundShape
import com.example.yandexfoodtechcart.ui.theme.CartBottomBackgroundShape
import com.example.yandexfoodtechcart.ui.theme.CartItemShape
import com.example.yandexfoodtechcart.ui.theme.CounterBackgroundShape
import com.example.yandexfoodtechcart.ui.theme.PrimaryColor
import com.example.yandexfoodtechcart.ui.theme.PrimaryTextColor
import com.example.yandexfoodtechcart.ui.theme.Typography

@Composable
@Preview
fun PreviewCartScreenContent() {
    CartScreenContent(
        items = initialCartItems,
        isLoading = false,
        onEvent = {},
        modifier = Modifier
    )
}

@Composable
@Preview
fun PreviewCartScreenContentLoading() {
    CartScreenContent(
        items = initialCartItems,
        isLoading = true,
        onEvent = {},
        modifier = Modifier
    )
}

@Composable
fun CartScreen(
    viewModel: CartViewModel = viewModel(factory = CartViewModel.Factory),
    onGoBack: () -> Unit,
    modifier: Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val uiEffect by viewModel.uiEffect.collectAsStateWithLifecycle()
    when (uiEffect) {
        CartScreenEffect.OnBack -> onGoBack.invoke()
        is CartScreenEffect.ShowErrorMessage ->
            showShortToast(
                LocalContext.current,
                (uiEffect as CartScreenEffect.ShowErrorMessage).message
            )

        null -> Unit
    }

    CartScreenContent(
        items = uiState.cartItems,
        isLoading = uiState.isLoading,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
fun CartScreenContent(
    items: List<CartItem>,
    isLoading: Boolean,
    onEvent: (CartScreenEvent) -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier.background(color = BackgroundGrayColor)) {
        Surface(color = Color.White, shape = CartBackgroundShape) {
            Column {
                CartToolbar(
                    onGoBack = { onEvent.invoke(CartScreenEvent.OnGoBack) },
                    onClearCart = { onEvent.invoke(CartScreenEvent.OnClearCart) },
                    modifier = Modifier.fillMaxWidth()
                )
                CartHeader(
                    dishCount = items.size,
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
                )
                CartItemList(items = items, isLoading = isLoading, onEvent = onEvent)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Surface(
            color = Color.White,
            shape = CartBottomBackgroundShape,
            modifier = Modifier.fillMaxWidth()
        ) {
            NextButton(
                onClick = { onEvent.invoke(CartScreenEvent.OnCartNext) },
                isLoading = isLoading,
                items = items
            )
        }
    }
}

@Composable
fun CartHeader(dishCount: Int, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.cart_title),
            style = Typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryTextColor
            )
        )
        Text(
            text = stringResource(id = R.string.cart_dish_count, dishCount),
            style = Typography.bodyMedium.copy(
                color = PrimaryTextColor
            ),
            color = Color.Gray
        )
    }
}

@Composable
fun CartToolbar(
    onGoBack: () -> Unit,
    onClearCart: () -> Unit,
    modifier: Modifier
) {
    Box(modifier = modifier) {
        IconButton(
            onClick = onGoBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
            )
        }
        IconButton(
            onClick = onClearCart,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun CartItemList(
    items: List<CartItem>,
    isLoading: Boolean,
    onEvent: (CartScreenEvent) -> Unit
) {
    LazyColumn {
        itemsIndexed(items) { index, item ->
            if (index > 0) {
                CartDivider(modifier = Modifier.padding(start = 72.dp, end = 16.dp))
            }

            CartItem(
                item = item,
                isLoading = isLoading,
                onDecrement = { onEvent.invoke(CartScreenEvent.OnDecrementCount(item)) },
                onIncrement = { onEvent.invoke(CartScreenEvent.OnIncrementCount(item)) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun CartDivider(modifier: Modifier) {
    HorizontalDivider(thickness = 0.5.dp, color = BackgroundDividerColor, modifier = modifier)
}

@Composable
fun CartItem(
    item: CartItem,
    isLoading: Boolean,
    onDecrement: (String) -> Unit,
    onIncrement: (String) -> Unit,
    modifier: Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = item.imageUrl,
            error = painterResource(id = R.drawable.error_image),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .size(48.dp)
                .clip(CartItemShape)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, end = 16.dp)
        ) {
            Text(
                text = item.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = Typography.bodySmall
            )
            Text(
                text = "${item.price} ${item.currency}",
                style = Typography.labelSmall
            )
        }
        Counter(
            count = item.count,
            onDecrement = {
                if (!isLoading) {
                    onDecrement.invoke(item.id)
                }
            },
            onIncrement = {
                if (!isLoading) {
                    onIncrement.invoke(item.id)
                }
            },
            modifier = Modifier
        )
    }
}

@Composable
fun Counter(
    count: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    minCount: Int = 1,
    maxCount: Int = 99,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .clip(shape = CounterBackgroundShape)
            .background(color = BackgroundCounterColor)
            .padding(all = 4.dp)
    ) {
        Image(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier
                .size(8.dp)
                .clickable {
                    if (count > minCount) {
                        onDecrement.invoke()
                    }
                }
        )
        Text(text = count.toString(), style = Typography.bodySmall)
        Image(
            imageVector = Icons.Filled.KeyboardArrowUp,
            contentDescription = null,
            modifier = Modifier
                .size(8.dp)
                .clickable {
                    if (count < maxCount) {
                        onIncrement.invoke()
                    }
                }
        )
    }
}

@Composable
private fun NextButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    items: List<CartItem>
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(72.dp)
            .padding(8.dp),
        shape = ButtonShape,
        enabled = !isLoading && items.isNotEmpty(),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryColor,
            contentColor = PrimaryTextColor
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val totalPrice = items.sumOf { it.count * it.price }
                val currency = items.firstOrNull()?.currency
                Text(
                    text = stringResource(id = R.string.next),
                    modifier = Modifier.align(Alignment.Center)
                )
                if (currency != null) {
                    Text(
                        text = stringResource(id = R.string.price, totalPrice, currency),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}

private fun showShortToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
