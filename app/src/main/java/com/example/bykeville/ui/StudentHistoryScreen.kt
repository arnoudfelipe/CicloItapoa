package com.example.bykeville.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bykeville.R
import com.example.bykeville.data.DataRepository
import com.example.bykeville.data.LoanHistory
import com.example.bykeville.data.Resource
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHistoryScreen(
    userId: String,
    onBack: () -> Unit = {}
) {
    val repository = remember { DataRepository() }
    val historyResource by repository.getLoanHistory(userId).collectAsState(initial = Resource.Loading())

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.fundoapp),
                contentScale = ContentScale.FillBounds
            ),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Histórico de Empréstimos",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.DarkGray,
                    navigationIconContentColor = Color.DarkGray
                )
            )
        }
    ) { paddingValues ->
        when (val resource = historyResource) {
            is Resource.Loading -> LoadingState(paddingValues)
            is Resource.Success -> HistoryList(resource.data, paddingValues)
            is Resource.Error -> ErrorState(resource.message ?: "Erro desconhecido", paddingValues)
        }
    }
}

@Composable
fun LoadingState(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text("Carregando histórico...")
    }
}

@Composable
fun ErrorState(message: String, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Erro ao carregar:", color = MaterialTheme.colorScheme.error)
        Text(message)
    }
}

@Composable
fun HistoryList(history: List<LoanHistory>, paddingValues: PaddingValues) {
    if (history.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Nenhum empréstimo registrado.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
    ) {
        items(history) { item ->
            LoanItemCard(item)
        }
    }
}

@Composable
fun LoanItemCard(item: LoanHistory) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bicicleta: ${item.bikeNumber}",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Empréstimo: ${item.loanDate?.let { dateFormatter.format(it) } ?: "Data indisponível"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Entrega: ${item.returnDate?.let { dateFormatter.format(it) } ?: "Pendente"}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (item.returnDate == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}