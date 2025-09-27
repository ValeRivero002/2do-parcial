package com.calyrsoft.ucbp1.features.dollar.domain.model

data class DollarModel(
    var dollarOfficial: String? = null,   // compat
    var dollarParallel: String? = null,   // compat

    // NUEVOS CAMPOS
    var officialBuy: String? = null,
    var officialSell: String? = null,
    var parallelBuy: String? = null,
    var parallelSell: String? = null,

    var timestamp: Long = 0L
)
