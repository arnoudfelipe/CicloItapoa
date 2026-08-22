package com.example.bykeville.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// Adicionei um novo estado para a tela de histórico do aluno
enum class DirectorScreenRoute {
    HOME,
    CADASTRO_FUNCIONARIO,
    CADASTRO_BICICLETA,
    CADASTRO_ALUNO,
    GERENCIAR_ALUNO,
    GERENCIAR_BICICLETA,
    HISTORICO_ALUNO
}

@Composable
fun DirectorNavigation(
    userId: String,
    onLogout: () -> Unit = {}
) {
    // Usando a nova rota interna criada acima
    var currentScreen by remember { mutableStateOf(DirectorScreenRoute.HOME) }

    // Variável para guardar o ID do aluno quando clicado na lista
    var selectedStudentId by remember { mutableStateOf<String?>(null) }

    when (currentScreen) {
        DirectorScreenRoute.HOME -> DirectorMenuScreen(
            // Mapeando a tela do menu para a rota interna
            onNavigate = { screen ->
                currentScreen = when (screen) {
                    DirectorScreen.CADASTRO_FUNCIONARIO -> DirectorScreenRoute.CADASTRO_FUNCIONARIO
                    DirectorScreen.CADASTRO_BICICLETA -> DirectorScreenRoute.CADASTRO_BICICLETA
                    DirectorScreen.CADASTRO_ALUNO -> DirectorScreenRoute.CADASTRO_ALUNO
                    DirectorScreen.GERENCIAR_ALUNO -> DirectorScreenRoute.GERENCIAR_ALUNO
                    DirectorScreen.GERENCIAR_BICICLETA -> DirectorScreenRoute.GERENCIAR_BICICLETA
                    else -> DirectorScreenRoute.HOME
                }
            },
            userId = userId,
            onBack = onLogout
        )

        DirectorScreenRoute.CADASTRO_FUNCIONARIO -> EmployeeRegistrationScreen(
            onBack = { currentScreen = DirectorScreenRoute.HOME }
        )

        DirectorScreenRoute.CADASTRO_BICICLETA -> BikeRegistrationScreen(
            onBack = { currentScreen = DirectorScreenRoute.HOME }
        )

        DirectorScreenRoute.CADASTRO_ALUNO -> StudentRegistrationScreen(
            onBack = { currentScreen = DirectorScreenRoute.HOME }
        )

        DirectorScreenRoute.GERENCIAR_ALUNO -> StudentListScreen(
            onBack = { currentScreen = DirectorScreenRoute.HOME },
            onStudentClick = { studentId ->
                selectedStudentId = studentId
                currentScreen = DirectorScreenRoute.HISTORICO_ALUNO
            }
        )

        // Nova rota para exibir o histórico do aluno selecionado
        DirectorScreenRoute.HISTORICO_ALUNO -> {
            selectedStudentId?.let { id ->
                StudentHistoryScreen(
                    userId = id,
                    onBack = { currentScreen = DirectorScreenRoute.GERENCIAR_ALUNO }
                )
            }
        }

        DirectorScreenRoute.GERENCIAR_BICICLETA -> BikeListScreen(
            onBack = { currentScreen = DirectorScreenRoute.HOME }
        )
    }
}