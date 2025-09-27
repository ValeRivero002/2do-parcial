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
                    // Agrega timestamp actual si no viene de Firebase
                    val dollarWithTimestamp = value.copy(
                        timestamp = if (value.timestamp == 0L) System.currentTimeMillis() else value.timestamp
                    )
                    trySend(dollarWithTimestamp)
                } else {
                    // dentro de onDataChange, en el else:
                    trySend(DollarModel("0", "0", "0", "0", "0", "0", System.currentTimeMillis()))
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