package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

fun getIconByName(name: String): ImageVector {
    return when (name) {
        "restaurant" -> Icons.Default.Restaurant
        "home" -> Icons.Default.Home
        "directions_bus" -> Icons.Default.DirectionsBus
        "receipt_long" -> Icons.Default.ReceiptLong
        "school" -> Icons.Default.School
        "health_and_safety" -> Icons.Default.HealthAndSafety
        "family_restroom" -> Icons.Default.FamilyRestroom
        "shopping_bag" -> Icons.Default.ShoppingBag
        "celebration" -> Icons.Default.Celebration
        "credit_card" -> Icons.Default.CreditCard
        "mosque" -> Icons.Default.Mosque
        "business_center" -> Icons.Default.BusinessCenter
        "category" -> Icons.Default.Category
        
        "work" -> Icons.Default.Work
        "account_balance_wallet" -> Icons.Default.AccountBalanceWallet
        "menu_book" -> Icons.Default.MenuBook
        "laptop_mac" -> Icons.Default.LaptopMac
        "storefront" -> Icons.Default.Storefront
        "redeem" -> Icons.Default.Redeem
        "schedule" -> Icons.Default.Schedule
        "card_giftcard" -> Icons.Default.CardGiftcard
        "flight" -> Icons.Default.Flight
        "trending_up" -> Icons.Default.TrendingUp
        "replay" -> Icons.Default.Replay
        "apartment" -> Icons.Default.Apartment
        "add_circle" -> Icons.Default.AddCircle
        "remove_circle" -> Icons.Default.RemoveCircle
        "handshake" -> Icons.Default.Handshake
        "savings" -> Icons.Default.Savings
        "settings" -> Icons.Default.Settings
        "notifications" -> Icons.Default.Notifications
        "file_download" -> Icons.Default.FileDownload
        "file_upload" -> Icons.Default.FileUpload
        "delete_forever" -> Icons.Default.DeleteForever
        "warning" -> Icons.Default.Warning
        "check_circle" -> Icons.Default.CheckCircle
        else -> Icons.Default.Help
    }
}
