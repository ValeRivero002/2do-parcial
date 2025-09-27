package com.calyrsoft.ucbp1.features.dollar.data.repository

import com.calyrsoft.ucbp1.features.dollar.data.datasource.RealTimeRemoteDataSource
import com.calyrsoft.ucbp1.features.dollar.data.datasource.DollarLocalDataSource
import com.calyrsoft.ucbp1.features.dollar.domain.model.DollarModel
import com.calyrsoft.ucbp1.features.dollar.domain.repository.IDollarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class DollarRepositoryImpl(
    private val realTimeRemoteDataSource: RealTimeRemoteDataSource,
    private val localDataSource: DollarLocalDataSource
) : IDollarRepository {

    override fun getDollar(): Flow<DollarModel> {
        return realTimeRemoteDataSource.getDollarUpdates()
            .onEach { dollar ->
                // Guardar en base de datos local cada actualización
                localDataSource.insert(dollar)
            }
    }


}