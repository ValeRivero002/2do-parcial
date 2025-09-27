package com.calyrsoft.ucbp1.features.dollar.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calyrsoft.ucbp1.features.dollar.data.datasource.DollarLocalDataSource
import com.calyrsoft.ucbp1.features.dollar.domain.model.DollarModel
import com.calyrsoft.ucbp1.features.dollar.domain.usecase.FetchDollarUseCase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DollarViewModel(
    val fetchDollarUseCase: FetchDollarUseCase,
    private val localDataSource: DollarLocalDataSource
): ViewModel() {

    sealed class DollarUIState {
        object Loading : DollarUIState()
        class Error(val message: String) : DollarUIState()
        class Success(val data: DollarModel, val history: List<DollarModel>) : DollarUIState()
    }

    init {
        getDollar()
        loadHistory()
    }

    private val _uiState = MutableStateFlow<DollarUIState>(DollarUIState.Loading)
    val uiState: StateFlow<DollarUIState> = _uiState.asStateFlow()

    fun getDollar() {
        viewModelScope.launch {

            fetchDollarUseCase()
                .catch { e ->
                    _uiState.value = DollarUIState.Error("Error: ${e.message}")
                }
                .collect { dollar ->
                    // Mantener el historial actual mientras se actualizan los datos en tiempo real
                    val currentHistory = (_uiState.value as? DollarUIState.Success)?.history ?: emptyList()
                    _uiState.value = DollarUIState.Success(dollar, currentHistory)
                }
        }
    }
    fun loadHistory() {
        viewModelScope.launch {
            try {
                val history = localDataSource.getAllOrderedByDate()
                val currentData = (_uiState.value as? DollarUIState.Success)?.data ?: DollarModel("0", "0")
                _uiState.value = DollarUIState.Success(currentData, history)
            } catch (e: Exception) {
                // No cambiar el estado si falla la carga del histórico
            }
        }
    }

}