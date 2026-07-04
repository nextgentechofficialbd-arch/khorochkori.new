package com.example.data.model

data class Category(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val iconName: String // standard Material Icon string
)

val EXPENSE_CATEGORIES = listOf(
    Category("food", "খাবার", "Food", "restaurant"),
    Category("home", "বাসস্থান", "Home", "home"),
    Category("transport", "যাতায়াত", "Transport", "directions_bus"),
    Category("bills", "বিল", "Bills", "receipt_long"),
    Category("education", "শিক্ষা", "Education", "school"),
    Category("health", "স্বাস্থ্য", "Health", "health_and_safety"),
    Category("family", "পরিবার", "Family", "family_restroom"),
    Category("shopping", "শপিং", "Shopping", "shopping_bag"),
    Category("entertainment", "বিনোদন", "Entertainment", "celebration"),
    Category("financial", "আর্থিক দায়", "Financial", "credit_card"),
    Category("religious", "ধর্মীয়", "Religious", "mosque"),
    Category("business", "ব্যবসা", "Business", "business_center"),
    Category("other", "অন্যান্য", "Other", "category")
)

val INCOME_CATEGORIES = listOf(
    Category("salary", "বেতন", "Salary", "work"),
    Category("pocket", "হাত খরচ", "Pocket Money", "account_balance_wallet"),
    Category("tuition", "টিউশনি", "Tuition", "menu_book"),
    Category("freelance", "ফ্রিল্যান্স", "Freelance", "laptop_mac"),
    Category("business", "ব্যবসা", "Business", "storefront"),
    Category("bonus", "বোনাস", "Bonus", "redeem"),
    Category("parttime", "পার্ট-টাইম", "Part-time", "schedule"),
    Category("gift", "উপহার", "Gift", "card_giftcard"),
    Category("remittance", "রেমিট্যান্স", "Remittance", "flight"),
    Category("investment", "বিনিয়োগ", "Investment", "trending_up"),
    Category("refund", "রিফান্ড", "Refund", "replay"),
    Category("rent", "ভাড়া আয়", "Rent", "apartment"),
    Category("other", "অন্যান্য", "Other", "add_circle")
)

val PAYMENT_METHODS = listOf("bKash", "Nagad", "Rocket", "নগদ টাকা", "ব্যাংক কার্ড", "ব্যাংক ট্রান্সফার")

val USER_TYPE_LABELS = mapOf(
    "student" to "স্টুডেন্ট",
    "job" to "চাকরিজীবী",
    "freelance" to "ফ্রিল্যান্সার/ব্যবসায়ী",
    "family" to "পরিবার পরিচালক"
)
