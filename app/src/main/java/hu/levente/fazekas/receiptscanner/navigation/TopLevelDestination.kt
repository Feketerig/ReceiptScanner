package hu.levente.fazekas.receiptscanner.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector
) {
    Receipt(
        title = "Receipts",
        selectedIcon = Icons.Default.CreditCard,
        unSelectedIcon = Icons.Outlined.CreditCard
    ),
    Items(
        title = "Items",
        selectedIcon = Icons.Default.ShoppingBasket,
        unSelectedIcon = Icons.Outlined.ShoppingBasket
    ),
    Options(
        title = "Settings",
        selectedIcon = Icons.Default.Settings,
        unSelectedIcon = Icons.Outlined.Settings
    )
}