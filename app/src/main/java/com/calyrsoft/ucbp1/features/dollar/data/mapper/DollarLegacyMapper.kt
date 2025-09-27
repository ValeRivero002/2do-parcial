package com.calyrsoft.ucbp1.features.dollar.data.mapper

import com.calyrsoft.ucbp1.features.dollar.domain.model.DollarModel
import com.calyrsoft.ucbp1.features.dollar.domain.model.DollarRate

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

/** Convierte DollarModel (strings) → DollarRate (4 números + timestamp generado) */
fun DollarModel.toDollarRate(
    nowProvider: () -> Long = { System.currentTimeMillis() }
): DollarRate {
    val (oBuy, oSell) = parsePair(dollarOfficial)
    val (pBuy, pSell) = parsePair(dollarParallel)
    return DollarRate(
        officialBuy = oBuy,
        officialSell = oSell,
        parallelBuy = pBuy,
        parallelSell = pSell,
        updatedAt = nowProvider()
    )
}
