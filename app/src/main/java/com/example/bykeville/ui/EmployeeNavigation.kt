package com.example.bykeville.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// Nova enumeração exclusiva para controle interno da navegação do funcionário.
enum class EmployeeScreenRoute {
    HOME, CADASTRO_ALUNO, LISTA_ALUNOS, LISTA_BICICLETAS, HISTORICO_ALUNO
}

@Composable
fun EmployeeNavigation(
    userId: String,
    onLogout: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf(EmployeeScreenRoute.HOME) }
    var selectedStudentId by remember { mutableStateOf<String?>(null) }

    when (currentScreen) {
        EmployeeScreenRoute.HOME -> EmployeeMenuScreen(
            onNavigate = { screen ->
                currentScreen = when (screen) {
                    EmployeeScreen.CADASTRO_ALUNO -> EmployeeScreenRoute.CADASTRO_ALUNO
                    EmployeeScreen.LISTA_ALUNOS -> EmployeeScreenRoute.LISTA_ALUNOS
                    EmployeeScreen.LISTA_BICICLETAS -> EmployeeScreenRoute.LISTA_BICICLETAS
                    else -> EmployeeScreenRoute.HOME
                }
            },
            userId = userId,
            onBack = onLogout
        )

        EmployeeScreenRoute.CADASTRO_ALUNO -> StudentRegistrationScreen(
            onBack = { currentScreen = EmployeeScreenRoute.HOME }
        )

        EmployeeScreenRoute.LISTA_ALUNOS -> StudentListScreen(
            onBack = { currentScreen = EmployeeScreenRoute.HOME },
            onStudentClick = { studentId ->
                selectedStudentId = studentId
                currentScreen = EmployeeScreenRoute.HISTORICO_ALUNO
            }
        )

        // Chamada direta para a tela, sem usar Scaffold extra.
        EmployeeScreenRoute.HISTORICO_ALUNO -> {
            selectedStudentId?.let { id ->
                StudentHistoryScreen(
                    userId = id,
                    onBack = { currentScreen = EmployeeScreenRoute.LISTA_ALUNOS }
                )
            }
        }

        EmployeeScreenRoute.LISTA_BICICLETAS -> BikeListScreen(
            onBack = { currentScreen = EmployeeScreenRoute.HOME }
        )
    }
}