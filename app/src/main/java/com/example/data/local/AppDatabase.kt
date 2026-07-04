package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.UserProfile
import com.example.data.model.TransactionEntity
import com.example.data.model.LendBorrowEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.InvestmentEntity

@Database(
    entities = [
        UserProfile::class,
        TransactionEntity::class,
        LendBorrowEntity::class,
        SavingsGoalEntity::class,
        InvestmentEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun transactionDao(): TransactionDao
    abstract fun lendBorrowDao(): LendBorrowDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun investmentDao(): InvestmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `investments` (
                        `id` TEXT NOT NULL, 
                        `platformName` TEXT NOT NULL, 
                        `amount` REAL NOT NULL, 
                        `date` TEXT NOT NULL, 
                        `returnRate` REAL, 
                        `status` TEXT NOT NULL, 
                        `note` TEXT NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "khorochkori_database"
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
