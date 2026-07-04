package com.example.data.local

import androidx.room.*
import com.example.data.model.InvestmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentDao {
    @Query("SELECT * FROM investments ORDER BY date DESC, id DESC")
    fun getAllInvestments(): Flow<List<InvestmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(investment: InvestmentEntity)

    @Query("DELETE FROM investments WHERE id = :id")
    suspend fun deleteInvestmentById(id: String)

    @Query("DELETE FROM investments")
    suspend fun clearInvestments()
}
