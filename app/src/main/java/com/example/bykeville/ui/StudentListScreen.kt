package com.example.bykeville.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bykeville.R
import com.example.bykeville.data.DataRepository
import com.example.bykeville.data.Resource
import com.example.bykeville.data.StudentData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(
    onBack: () -> Unit,
    onStudentClick: (String) -> Unit
) {
    val repository = remember { DataRepository() }

    val studentsResource by repository.getAllStudents().collectAsState(initial = Resource.Loading())

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
                title = { Text("Gerenciar Alunos") },
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
        when (val resource = studentsResource) {
            is Resource.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Text("Buscando alunos...")
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
                StudentList(
                    students = resource.data,
                    paddingValues = paddingValues,
                    onStudentClick = onStudentClick
                )
            }
        }
    }
}

@Composable
fun StudentList(
    students: List<StudentData>,
    paddingValues: PaddingValues,
    onStudentClick: (String) -> Unit
) {
    if (students.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Nenhum aluno cadastrado.")
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
        items(students) { student ->
            StudentItemCard(student = student, onClick = {
                if (student.id.isNotBlank()) {
                    onStudentClick(student.id)
                }
            })
        }
    }
}

@Composable
fun StudentItemCard(student: StudentData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(student.nome, style = MaterialTheme.typography.titleMedium)
                Text(
                    "Matrícula: ${student.matricula}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Escola: ${student.escola}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Ver detalhes",
                modifier = Modifier.graphicsLayer(rotationZ = 180f)
            )
        }
    }
}