package com.calyrsoft.ucbp1.features.dollar.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.calyrsoft.ucbp1.features.dollar.data.database.entity.DollarHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DollarHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DollarHistoryEntity)

    @Query("SELECT * FROM dollar_history ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<DollarHistoryEntity>>

    @Query("SELECT * FROM dollar_history ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLatest(): DollarHistoryEntity?
}
