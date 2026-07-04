package com.example.data.local

import androidx.room.*
import com.example.data.model.LendBorrowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LendBorrowDao {
    @Query("SELECT * FROM lend_borrow ORDER BY date DESC, id DESC")
    fun getAllLendBorrows(): Flow<List<LendBorrowEntity>>

    @Query("SELECT * FROM lend_borrow ORDER BY date DESC, id DESC")
    suspend fun getAllLendBorrowsSync(): List<LendBorrowEntity>

    @Query("SELECT * FROM lend_borrow WHERE id = :id LIMIT 1")
    suspend fun getLendBorrowById(id: String): LendBorrowEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLendBorrow(lendBorrow: LendBorrowEntity)

    @Query("DELETE FROM lend_borrow WHERE id = :id")
    suspend fun deleteLendBorrowById(id: String)

    @Query("DELETE FROM lend_borrow")
    suspend fun clearLendBorrows()
}
