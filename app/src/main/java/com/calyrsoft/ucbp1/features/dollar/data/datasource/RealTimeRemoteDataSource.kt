package com.calyrsoft.ucbp1.features.dollar.data.datasource

import com.calyrsoft.ucbp1.features.dollar.domain.model.DollarModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RealTimeRemoteDataSource {

    fun getDollarUpdates(): Flow<DollarModel> = callbackFlow {
        val callback = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                // ELIMINA EL TODO() y cierra el flow con el error
                close(error.toException())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(DollarModel::class.java)
                if (value != null) {
                    val withDefaults = value.copy(
                        // timestamp seguro
                        timestamp = if (value.timestamp == 0L) System.currentTimeMillis() else value.timestamp,
                        // fallbacks: si no hay compra/venta en Firebase, usa el oficial/paralelo
                        officialBuy   = value.officialBuy   ?: value.dollarOfficial,
                        officialSell  = value.officialSell  ?: value.dollarOfficial,
                        parallelBuy   = value.parallelBuy   ?: value.dollarParallel,
                        parallelSell  = value.parallelSell  ?: value.dollarParallel
                    )
                    trySend(withDefaults)
                } else {
                    trySend(
                        DollarModel(
                            dollarOfficial = "0",
                            dollarParallel = "0",
                            officialBuy = "0",
                            officialSell = "0",
                            parallelBuy = "0",
                            parallelSell = "0",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }

        }

        val database = Firebase.database
        val myRef = database.getReference("dollar")
        myRef.addValueEventListener(callback)

        awaitClose {
            myRef.removeEventListener(callback)
        }
    }
}