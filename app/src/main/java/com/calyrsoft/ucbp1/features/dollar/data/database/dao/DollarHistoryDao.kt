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
    suspend fun insert(entity: DollarHistoryEntity)

    @Query("SELECT * FROM dollar_history ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<DollarHistoryEntity>>
}
