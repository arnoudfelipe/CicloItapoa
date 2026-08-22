package com.example.bykeville.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bykeville.R

enum class EmployeeScreen {
    HOME, CADASTRO_ALUNO, LISTA_ALUNOS, LISTA_BICICLETAS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeMenuScreen(
    onNavigate: (EmployeeScreen) -> Unit,
    userId: String,
    onBack: () -> Unit = {}
) {
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
                title = { Text("Menu do Funcionário") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            ActionCard(title = "Cadastrar Novo Aluno") {
                onNavigate(EmployeeScreen.CADASTRO_ALUNO)
            }
            ActionCard(title = "Gerenciar Alunos") {
                onNavigate(EmployeeScreen.LISTA_ALUNOS)
            }
            ActionCard(title = "Gerenciar Bicicletas") {
                onNavigate(EmployeeScreen.LISTA_BICICLETAS)
            }
        }
    }
}

@Composable
fun ActionCard(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = onClick,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Acessar")
            }
        }
    }
}