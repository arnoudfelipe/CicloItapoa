package com.example.bykeville.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bykeville.R
import com.example.bykeville.data.Bike
import com.example.bykeville.data.DataRepository
import com.example.bykeville.data.Resource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeListScreen(
    onBack: () -> Unit
) {
    val repository = remember { DataRepository() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val bikesResource by repository.getAllBikes().collectAsState(initial = Resource.Loading())

    var selectedBike by remember { mutableStateOf<Bike?>(null) }
    var showLendDialog by remember { mutableStateOf(false) }
    var showReturnDialog by remember { mutableStateOf(false) }

    var isLoadingAction by remember { mutableStateOf(false) }

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
                title = { Text("Gerenciar Bicicletas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
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
        when (val resource = bikesResource) {
            is Resource.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Text("Buscando bicicletas...")
                }
            }
            is Resource.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Erro: ${resource.message}", color = MaterialTheme.colorScheme.error)
                }
            }
            is Resource.Success -> {
                BikeList(
                    bikes = resource.data,
                    paddingValues = paddingValues,
                    onLendClick = { bike ->
                        selectedBike = bike
                        showLendDialog = true
                    },
                    onReturnClick = { bike ->
                        selectedBike = bike
                        showReturnDialog = true
                    }
                )
            }
        }
    }

    if (showLendDialog && selectedBike != null) {
        LendDialog(
            bike = selectedBike!!,
            isLoading = isLoadingAction,
            onDismiss = { showLendDialog = false },
            onConfirm = { matriculaAluno ->
                isLoadingAction = true
                coroutineScope.launch {
                    val result = repository.lendBike(selectedBike!!, matriculaAluno)
                    when (result) {
                        is Resource.Success -> {
                            Toast.makeText(context, "Bicicleta emprestada!", Toast.LENGTH_SHORT).show()
                            showLendDialog = false
                        }
                        is Resource.Error -> {
                            Toast.makeText(context, "Erro: ${result.message}", Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                    isLoadingAction = false
                }
            }
        )
    }

    if (showReturnDialog && selectedBike != null) {
        ReturnDialog(
            bike = selectedBike!!,
            isLoading = isLoadingAction,
            onDismiss = { showReturnDialog = false },
            onConfirm = {
                isLoadingAction = true
                coroutineScope.launch {
                    val result = repository.returnBike(selectedBike!!)
                    when (result) {
                        is Resource.Success -> {
                            Toast.makeText(context, "Bicicleta devolvida!", Toast.LENGTH_SHORT).show()
                            showReturnDialog = false
                        }
                        is Resource.Error -> {
                            Toast.makeText(context, "Erro: ${result.message}", Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                    isLoadingAction = false
                }
            }
        )
    }
}

@Composable
fun BikeList(
    bikes: List<Bike>,
    paddingValues: PaddingValues,
    onLendClick: (Bike) -> Unit,
    onReturnClick: (Bike) -> Unit
) {
    if (bikes.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Nenhuma bicicleta cadastrada.", color = Color.DarkGray)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(bikes) { bike ->
            BikeItemCard(
                bike = bike,
                onLendClick = { onLendClick(bike) },
                onReturnClick = { onReturnClick(bike) }
            )
        }
    }
}

@Composable
fun BikeItemCard(
    bike: Bike,
    onLendClick: () -> Unit,
    onReturnClick: () -> Unit
) {
    val isAvailable = bike.status.equals("disponivel", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(bike.numero, style = MaterialTheme.typography.titleMedium, color = Color.DarkGray)

                if (isAvailable) {
                    Text(
                        "Status: Disponível",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF2E7D32)
                    )
                } else {
                    Text(
                        "Status: Emprestada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        "Aluno: ${bike.alunoEmprestadoNome ?: "Não identificado"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )

                    if (!bike.escolaNome.isNullOrBlank()) {
                        Text(
                            "Escola: ${bike.escolaNome}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            if (isAvailable) {
                Button(onClick = onLendClick) {
                    Text("Emprestar")
                }
            } else {
                Button(
                    onClick = onReturnClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Devolver")
                }
            }
        }
    }
}

@Composable
fun LendDialog(
    bike: Bike,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (matricula: String) -> Unit
) {
    var matricula by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Emprestar Bicicleta") },
        text = {
            Column {
                Text("Bicicleta: ${bike.numero}")
                Text("Digite a matrícula do aluno:")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = matricula,
                    onValueChange = { matricula = it },
                    label = { Text("Matrícula do Aluno") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    enabled = !isLoading
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (matricula.isNotBlank()) {
                        onConfirm(matricula)
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Confirmar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ReturnDialog(
    bike: Bike,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Devolver Bicicleta") },
        text = {
            Text("Confirma a devolução da ${bike.numero}, emprestada para ${bike.alunoEmprestadoNome}?")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Confirmar Devolução")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancelar")
            }
        }
    )
}