package com.calyrsoft.ucbp1.features.dollar.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.calyrsoft.ucbp1.features.dollar.data.database.dao.IDollarDao
import com.calyrsoft.ucbp1.features.dollar.data.database.entity.DollarEntity
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [DollarEntity::class], version = 2)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun dollarDao(): IDollarDao

    companion object {
        @Volatile
        private var Instance: AppRoomDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE dollars ADD COLUMN official_buy TEXT")
                database.execSQL("ALTER TABLE dollars ADD COLUMN official_sell TEXT")
                database.execSQL("ALTER TABLE dollars ADD COLUMN parallel_buy TEXT")
                database.execSQL("ALTER TABLE dollars ADD COLUMN parallel_sell TEXT")
            }
        }

        fun getDatabase(context: Context): AppRoomDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppRoomDatabase::class.java, "dollar_db")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
