package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.FinanceRepository
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.FinanceViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Room Database, DAOs, and repository directly
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = FinanceRepository(
            database.userProfileDao(),
            database.transactionDao(),
            database.lendBorrowDao(),
            database.savingsGoalDao(),
            database.investmentDao(),
            applicationContext
        )
        
        // Instantiate the ViewModel
        val viewModel: FinanceViewModel = ViewModelProvider(
            this,
            FinanceViewModelFactory(repository)
        )[FinanceViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainAppScreen(viewModel = viewModel)
                }
            }
        }
    }
}
