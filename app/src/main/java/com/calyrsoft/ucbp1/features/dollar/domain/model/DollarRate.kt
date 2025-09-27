package com.calyrsoft.ucbp1.features.dollar.domain.model

/** Modelo actualizado con 4 campos + timestamp */
data class DollarRate(
    val officialBuy: Double,
    val officialSell: Double,
    val parallelBuy: Double,
    val parallelSell: Double,
    val updatedAt: Long
)
