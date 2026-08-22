package com.example.bykeville.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.bykeville.R
import com.example.bykeville.data.DataRepository
import com.example.bykeville.data.Escola
import com.example.bykeville.data.Resource
import com.example.bykeville.data.StudentData
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegistrationScreen(onBack: () -> Unit) {
    val repository = remember { DataRepository() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var escolasList by remember { mutableStateOf<List<Escola>>(emptyList()) }
    var selectedEscola by remember { mutableStateOf<Escola?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.DarkGray,
        unfocusedTextColor = Color.DarkGray,
        focusedLabelColor = Color.DarkGray,
        unfocusedLabelColor = Color.DarkGray,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedTrailingIconColor = Color.DarkGray,
        unfocusedTrailingIconColor = Color.DarkGray
    )

    LaunchedEffect(Unit) {
        repository.getAllEscolas().collect { result ->
            if (result is Resource.Success) {
                escolasList = result.data
            }
        }
    }

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
                title = { Text("Cadastrar Novo Aluno") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome Completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                colors = customTextFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = cpf,
                onValueChange = {
                    if (it.length <= 11 && it.all { char -> char.isDigit() }) {
                        cpf = it
                    }
                },
                label = { Text("CPF (Somente números, 11 dígitos)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                enabled = !isLoading,
                colors = customTextFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = matricula,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        matricula = it
                    }
                },
                label = { Text("Matrícula (Somente números)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                enabled = !isLoading,
                colors = customTextFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha de Acesso") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !isLoading,
                colors = customTextFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedEscola?.nome ?: "Selecione uma escola",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Escola") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    enabled = !isLoading,
                    colors = customTextFieldColors
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (escolasList.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Nenhuma escola cadastrada") },
                            onClick = { expanded = false }
                        )
                    } else {
                        escolasList.forEach { escola ->
                            DropdownMenuItem(
                                text = { Text(escola.nome) },
                                onClick = {
                                    selectedEscola = escola
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (nome.isBlank() || matricula.isBlank() || senha.isBlank() || cpf.isBlank() || selectedEscola == null) {
                        Toast.makeText(context, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (cpf.length != 11) {
                        Toast.makeText(context, "O CPF deve ter exatamente 11 dígitos.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    coroutineScope.launch {

                        val novoAluno = StudentData(
                            nome = nome,
                            cpf = cpf,
                            matricula = matricula,
                            senha = senha,
                            escola = selectedEscola!!.nome
                        )

                        when (val result = repository.registerStudent(novoAluno)) {
                            is Resource.Success -> {
                                Toast.makeText(context, "Aluno cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                                onBack()
                            }
                            is Resource.Error -> {
                                Toast.makeText(context, "Erro: ${result.message}", Toast.LENGTH_LONG).show()
                                isLoading = false
                            }
                            is Resource.Loading -> {}
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Salvar Cadastro")
                }
            }
        }
    }
}