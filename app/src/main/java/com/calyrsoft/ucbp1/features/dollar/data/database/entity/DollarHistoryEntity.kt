package com.calyrsoft.ucbp1.features.dollar.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.calyrsoft.ucbp1.features.dollar.domain.model.DollarRate

@Entity(tableName = "dollar_history")
data class DollarHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val officialBuy: Double,
    val officialSell: Double,
    val parallelBuy: Double,
    val parallelSell: Double,
    val updatedAt: Long
) {
    fun toDomain() = DollarRate(officialBuy, officialSell, parallelBuy, parallelSell, updatedAt)

    companion object {
        fun from(rate: DollarRate) = DollarHistoryEntity(
            officialBuy = rate.officialBuy,
            officialSell = rate.officialSell,
            parallelBuy = rate.parallelBuy,
            parallelSell = rate.parallelSell,
            updatedAt = rate.updatedAt
        )
    }
}
