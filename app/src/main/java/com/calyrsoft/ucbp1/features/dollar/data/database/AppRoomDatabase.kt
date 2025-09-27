// app/src/main/java/com/calyrsoft/ucbp1/features/dollar/data/database/AppRoomDatabase.kt
package com.calyrsoft.ucbp1.features.dollar.data.database

import androidx.room.Database
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "dollar_history")
data class DollarHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val officialBuy: Double,
    val officialSell: Double,
    val parallelBuy: Double,
    val parallelSell: Double,
    val updatedAt: Long
)

@Dao
interface DollarHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DollarHistoryEntity)

    @Query("SELECT * FROM dollar_history ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<DollarHistoryEntity>>

    @Query("SELECT * FROM dollar_history ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLatest(): DollarHistoryEntity?
}

@Database(
    entities = [DollarHistoryEntity::class],
    version = 2,                // súbelo respecto a tu versión previa
    exportSchema = true
)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun dollarHistoryDao(): DollarHistoryDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS dollar_history(
                      id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                      officialBuy REAL NOT NULL,
                      officialSell REAL NOT NULL,
                      parallelBuy REAL NOT NULL,
                      parallelSell REAL NOT NULL,
                      updatedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
