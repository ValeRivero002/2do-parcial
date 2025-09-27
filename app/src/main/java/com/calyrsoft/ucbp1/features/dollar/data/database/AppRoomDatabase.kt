// app/src/main/java/com/calyrsoft/ucbp1/features/dollar/data/database/AppRoomDatabase.kt
package com.calyrsoft.ucbp1.features.dollar.data.database

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/* ===================== AÑADE ESTO: ENTITY ===================== */
@Entity(tableName = "dollar_history")
data class DollarHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val officialBuy: Double,
    val officialSell: Double,
    val parallelBuy: Double,
    val parallelSell: Double,
    val updatedAt: Long
)

/* ===================== AÑADE ESTO: DAO ===================== */
@Dao
interface DollarHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DollarHistoryEntity)

    @Query("SELECT * FROM dollar_history ORDER BY updatedAt DESC")
    fun observeAll(): kotlinx.coroutines.flow.Flow<List<DollarHistoryEntity>>

    @Query("SELECT * FROM dollar_history ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLatest(): DollarHistoryEntity?
}

/* ========== TU Database: SOLO EDITA entities + version + migration ========== */
@Database(
    entities = [
        // ...tus otras entidades,
        DollarHistoryEntity::class // <-- AÑADIDO
    ],
    version = 2, // <-- SUBE LA VERSIÓN (ajusta desde la que tengas)
    exportSchema = true
)
abstract class AppRoomDatabase : RoomDatabase() {

    // ...tus DAOs existentes

    /* AÑADE ESTA FUNCIÓN: */
    abstract fun dollarHistoryDao(): DollarHistoryDao

    companion object {
        /* AÑADE/ACTUALIZA esta MIGRATION acorde a tu versión anterior -> 2 */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS dollar_history(
                      id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                      officialBuy REAL NOT NULL,
                      officialSell REAL NOT NULL,
                      parallelBuy REAL NOT NULL,
                      parallelSell REAL NOT NULL,
                      updatedAt INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }
    }
}
