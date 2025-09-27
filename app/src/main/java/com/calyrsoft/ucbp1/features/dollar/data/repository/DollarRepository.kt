package com.calyrsoft.ucbp1.features.dollar.data.repository

import com.calyrsoft.ucbp1.features.dollar.data.database.dao.DollarHistoryDao
import com.calyrsoft.ucbp1.features.dollar.data.database.entity.DollarHistoryEntity
import com.calyrsoft.ucbp1.features.dollar.domain.model.DollarModel
import com.calyrsoft.ucbp1.features.dollar.domain.repository.IDollarRepository
import com.calyrsoft.ucbp1.features.dollar.data.datasource.RealTimeRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class DollarRepository(
    private val realtime: RealTimeRemoteDataSource,
    private val historyDao: DollarHistoryDao   // ⬅️ inyectamos DAO de Room
) : IDollarRepository {

    override fun getDollar(): Flow<DollarModel> {
        return realtime.getDollarUpdates().onEach { legacy ->
            // Mapear tus strings oficiales/paralelos a números y guardarlos en histórico
            historyDao.insert(legacy.toHistoryEntity())
        }
    }

    /* ========== Mapper privado ========== */
    private fun parsePair(text: String?): Pair<Double, Double> {
        if (text.isNullOrBlank()) return 0.0 to 0.0
        val parts = text
            .replace(",", " ")
            .replace("|", " ")
            .replace("/", " ")
            .split(" ")
            .filter { it.isNotBlank() }
        val buy  = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
        val sell = parts.getOrNull(1)?.toDoubleOrNull() ?: buy
        return buy to sell
    }

    private fun DollarModel.toHistoryEntity(
        now: Long = System.currentTimeMillis()
    ): DollarHistoryEntity {
        val (oBuy, oSell) = parsePair(dollarOfficial)
        val (pBuy, pSell) = parsePair(dollarParallel)
        return DollarHistoryEntity(
            officialBuy = oBuy,
            officialSell = oSell,
            parallelBuy  = pBuy,
            parallelSell = pSell,
            updatedAt = now
        )
    }
}
