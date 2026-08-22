package com.example.bykeville.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bykeville.R
import com.example.bykeville.data.DataRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    val repository = remember { DataRepository() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var isAuthenticated by remember { mutableStateOf(false) }
    var currentUserId by remember { mutableStateOf("") }
    var currentProfile by remember { mutableStateOf(Perfil.ALUNO) }

    if (isAuthenticated) {
        MainNavigation(
            userId = currentUserId,
            perfil = currentProfile,
            onLogout = {
                isAuthenticated = false
                currentUserId = ""
            }
        )
        return
    }

    var selectedProfile by remember { mutableStateOf(Perfil.ALUNO) }
    var matricula by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val containerColor = Color(0xFFE0F7FA)

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = Color.Gray,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedTrailingIconColor = Color.Gray
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.fundoapp),
                contentScale = ContentScale.FillBounds
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.bykeville_logo),
                contentDescription = "Logo do Bykeville",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = stringResource(id = R.string.login_title),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = primaryColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    ProfileDropdownSelector(
                        selectedProfile = selectedProfile,
                        onProfileSelected = { selectedProfile = it },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = customTextFieldColors
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = matricula,
                        onValueChange = { matricula = it },
                        label = { Text(stringResource(id = R.string.hint_matricula)) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = customTextFieldColors
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = senha,
                        onValueChange = { senha = it },
                        label = { Text(stringResource(id = R.string.hint_senha)) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = customTextFieldColors
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (matricula.isBlank() || senha.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Preencha a matrícula e a senha.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            isLoading = true
                            coroutineScope.launch {
                                val loginResult = repository.loginUser(selectedProfile, matricula, senha)
                                isLoading = false

                                if (loginResult != null) {
                                    val uid = loginResult.first
                                    val nome = loginResult.second

                                    isAuthenticated = true
                                    currentUserId = uid
                                    currentProfile = selectedProfile
                                    Toast.makeText(
                                        context,
                                        "Login bem-sucedido! Bem-vindo(a), $nome!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Credenciais ou perfil inválidos. Tente novamente.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                stringResource(id = R.string.button_acessar),
                                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                            )
                        }
                    }
                }
            }
        }

        // Ícone de informação
        IconButton(
            onClick = { showInfoDialog = true },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Informações do App",
                tint = Color.DarkGray
            )
        }
    }

    // informações do app
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("Sobre o App") },
            text = {
                Column {
                    Text("CicloItapoá", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Versão: 1.0")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Desenvolvido por: Arnoud[4831113]")
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Fechar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDropdownSelector(
    selectedProfile: Perfil,
    onProfileSelected: (Perfil) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    var expanded by remember { mutableStateOf(false) }
    val profiles = Perfil.entries

    val profileNames = mapOf(
        Perfil.ALUNO to stringResource(id = R.string.profile_aluno),
        Perfil.FUNCIONARIO to stringResource(id = R.string.profile_funcionario),
        Perfil.DIRETOR to stringResource(id = R.string.profile_diretor)
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = profileNames[selectedProfile] ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.select_profile)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = colors,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            enabled = enabled
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            profiles.forEach { perfil ->
                DropdownMenuItem(
                    text = { Text(profileNames[perfil] ?: "") },
                    onClick = {
                        onProfileSelected(perfil)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}