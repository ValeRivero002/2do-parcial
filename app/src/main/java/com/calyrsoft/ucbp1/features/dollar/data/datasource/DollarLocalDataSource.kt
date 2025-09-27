package com.calyrsoft.ucbp1.features.dollar.data.datasource


import com.calyrsoft.ucbp1.features.dollar.data.database.dao.IDollarDao
import com.calyrsoft.ucbp1.features.dollar.data.mapper.toEntity
import com.calyrsoft.ucbp1.features.dollar.data.mapper.toModel
import com.calyrsoft.ucbp1.features.dollar.domain.model.DollarModel

class DollarLocalDataSource(
    val dao: IDollarDao
) {

    suspend fun getList(): List<DollarModel> {
        return dao.getList().map {
            it.toModel()
        }

    }
    suspend fun deleteAll() {
        dao.deleteAll()
    }
    suspend fun inserTDollars(list: List<DollarModel>) {
        val dollarEntity = list.map { it.toEntity() }
        dao.insertDollars(dollarEntity)
    }

    suspend fun insert(dollar: DollarModel) {
        dao.insert(dollar.toEntity())
    }
    suspend fun getAllOrderedByDate(): List<DollarModel> {
        return dao.getAllOrderedByDate().map { it.toModel() }
    }

    suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }


}