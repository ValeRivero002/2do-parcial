// DollarScreen.kt
package com.calyrsoft.ucbp1.features.dollar.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.calyrsoft.ucbp1.core.presentation.components.TopAppBarWithBack
import com.calyrsoft.ucbp1.features.dollar.domain.model.DollarModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DollarScreen(
    navController: NavController,
    viewModelDollar: DollarViewModel = koinViewModel()
) {
    val state by viewModelDollar.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBarWithBack(
                title = "Cotización del Dólar",
                navController = navController,
                actions = {
                    IconButton(onClick = { viewModelDollar.loadHistory() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (val stateValue = state) {
                is DollarViewModel.DollarUIState.Error -> {
                    Text(
                        text = stateValue.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                DollarViewModel.DollarUIState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(32.dp)
                    )
                }

                is DollarViewModel.DollarUIState.Success -> {
                    // Card con valores actuales (4 campos)
                    CurrentDollarCard(dollar = stateValue.data)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Historial
                    Text(
                        text = "Historial de Cambios",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    DollarHistoryList(history = stateValue.history)
                }
            }
        }
    }
}

@Composable
fun CurrentDollarCard(dollar: DollarModel) {
    // Parser tolerante: "6.96 / 7.06" | "6.96|7.06" | "6.96,7.06" | "6.96 7.06"
    fun parsePair(text: String?): Pair<Double, Double> {
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

    val (oBuy, oSell) = parsePair(dollar.dollarOfficial)
    val (pBuy, pSell) = parsePair(dollar.dollarParallel)

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Valor Actual",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            // Grid 2 × 2: Oficial/Paralelo × Compra/Venta
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    RateTile("Oficial • Compra", oBuy)
                    RateTile("Paralelo • Compra", pBuy)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    RateTile("Oficial • Venta", oSell)
                    RateTile("Paralelo • Venta", pSell)
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = "Actualizado: ${formatDate(dollar.timestamp)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun RateTile(label: String, value: Double) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (value == 0.0) "N/A" else String.format(Locale.US, "$ %.2f", value),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
@Composable
fun DollarHistoryList(history: List<DollarModel>) {
    if (history.isEmpty()) {
        Text(
            text = "No hay historial disponible",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(16.dp)
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(history) { dollar ->
                DollarHistoryItem(dollar = dollar)
            }
        }
    }
}

@Composable
fun DollarHistoryItem(dollar: DollarModel) {
    fun parsePair(text: String?): Pair<Double, Double> {
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

    val (oBuy, oSell) = parsePair(dollar.dollarOfficial)
    val (pBuy, pSell) = parsePair(dollar.dollarParallel)

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = formatDate(dollar.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Oficial", style = MaterialTheme.typography.labelLarge)
                    Text("Compra: ${if (oBuy == 0.0) "N/A" else String.format(Locale.US, "%.2f", oBuy)}")
                    Text("Venta:  ${if (oSell == 0.0) "N/A" else String.format(Locale.US, "%.2f", oSell)}")
                }
                Column {
                    Text("Paralelo", style = MaterialTheme.typography.labelLarge)
                    Text("Compra: ${if (pBuy == 0.0) "N/A" else String.format(Locale.US, "%.2f", pBuy)}")
                    Text("Venta:  ${if (pSell == 0.0) "N/A" else String.format(Locale.US, "%.2f", pSell)}")
                }
            }
        }
    }
}
// Utilidades
private fun formatDate(timestamp: Long): String {
    return try {
        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    } catch (e: Exception) {
        "Fecha inválida"
    }
}
