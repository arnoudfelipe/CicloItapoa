package com.example.bykeville.ui

import androidx.compose.runtime.Composable

/**
 * Componente principal de navegação que decide qual tela de menu
 * exibir após um login bem-sucedido, com base no perfil do usuário.
 *
 * @param userId O ID do documento do usuário no Firestore.
 * @param perfil O perfil (Aluno, Funcionário, Diretor) do usuário.
 * @param onLogout Callback para deslogar e retornar à tela de login.
 */
@Composable
fun MainNavigation(
    userId: String,
    perfil: Perfil,
    onLogout: () -> Unit = {}
) {

    when (perfil) {
        // PERFIL ALUNO: Chama a tela de histórico e conecta a seta de voltar ao logout
        Perfil.ALUNO -> StudentHistoryScreen(
            userId = userId,
            onBack = onLogout
        )

        // PERFIL FUNCIONÁRIO: Chama a navegação do funcionário
        Perfil.FUNCIONARIO -> EmployeeNavigation(userId = userId, onLogout = onLogout)

        // PERFIL DIRETOR: Chama a nova navegação do diretor
        Perfil.DIRETOR -> DirectorNavigation(userId = userId, onLogout = onLogout)
    }
}