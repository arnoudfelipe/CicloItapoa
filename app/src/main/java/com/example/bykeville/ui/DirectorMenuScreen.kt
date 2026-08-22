package com.example.bykeville.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

enum class DirectorScreen {
    HOME,
    CADASTRO_FUNCIONARIO,
    CADASTRO_BICICLETA,
    CADASTRO_ALUNO,
    GERENCIAR_ALUNO,
    GERENCIAR_BICICLETA
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectorMenuScreen(
    onNavigate: (DirectorScreen) -> Unit,
    userId: String,
    onBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.fundoapp),
                // Ajustado para forçar a imagem inteira a caber na tela
                contentScale = ContentScale.FillBounds
            ),
        topBar = {
            TopAppBar(
                title = { Text("Menu do Diretor") },
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
                    titleContentColor = Color.DarkGray, // Texto em cinza escuro
                    navigationIconContentColor = Color.DarkGray // Seta em cinza escuro
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            DirectorActionCard(title = "Cadastrar Novo Funcionário") {
                onNavigate(DirectorScreen.CADASTRO_FUNCIONARIO)
            }
            DirectorActionCard(title = "Cadastrar Nova Bicicleta") {
                onNavigate(DirectorScreen.CADASTRO_BICICLETA)
            }
            DirectorActionCard(title = "Cadastrar Novo Aluno") {
                onNavigate(DirectorScreen.CADASTRO_ALUNO)
            }
            DirectorActionCard(title = "Gerenciar Aluno") {
                onNavigate(DirectorScreen.GERENCIAR_ALUNO)
            }
            DirectorActionCard(title = "Gerenciar Bicicletas") {
                onNavigate(DirectorScreen.GERENCIAR_BICICLETA)
            }
        }
    }
}

@Composable
fun DirectorActionCard(title: String, onClick: () -> Unit) {
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