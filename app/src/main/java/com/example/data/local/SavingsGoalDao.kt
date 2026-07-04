package com.example.data.local

import androidx.room.*
import com.example.data.model.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsGoalDao {
    @Query("SELECT * FROM savings_goals ORDER BY id DESC")
    fun getAllGoals(): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM savings_goals ORDER BY id DESC")
    suspend fun getAllGoalsSync(): List<SavingsGoalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: SavingsGoalEntity)

    @Query("DELETE FROM savings_goals WHERE id = :id")
    suspend fun deleteGoalById(id: String)

    @Query("DELETE FROM savings_goals")
    suspend fun clearGoals()
}
