package com.example.bykeville

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.bykeville.ui.LoginScreen
import com.example.bykeville.ui.theme.BykevilleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Usa o tema principal do seu projeto
            BykevilleTheme {
                // A Surface é o container principal que usa as cores do tema.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Chamamos nosso componente principal aqui!
                    LoginScreen()
                }
            }
        }
    }
}