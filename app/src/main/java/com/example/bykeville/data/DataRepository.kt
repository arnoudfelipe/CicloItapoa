package com.example.bykeville.data

import com.example.bykeville.ui.Perfil
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import java.util.Date

sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String? = null) : Resource<T>()
}

data class LoanHistory(
    val bikeNumber: String = "",
    val loanDate: Date? = null,
    val returnDate: Date? = null
)

data class StudentData(
    @get:Exclude @set:Exclude var id: String = "",
    val nome: String = "",
    val cpf: String = "",
    val matricula: String = "",
    val senha: String = "",
    val escola: String = ""
)

data class EmployeeData(
    val nome: String = "",
    val cpf: String = "",
    val matricula: String = "",
    val senha: String = "",
    val escola: String = ""
)

data class Escola(
    @get:Exclude @set:Exclude var id: String = "",
    val nome: String = "",
    val endereco: String = ""
)

data class Bike(
    @get:Exclude @set:Exclude var id: String = "",
    val numero: String = "",
    val status: String = "disponivel",
    val alunoEmprestadoId: String? = null,
    val alunoEmprestadoNome: String? = null,
    val escolaId: String? = null,
    val escolaNome: String? = null
)

class DataRepository {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun loginUser(perfil: Perfil, matricula: String, senha: String): Pair<String, String>? {
        val collectionName = when (perfil) {
            Perfil.ALUNO -> "alunos"
            Perfil.FUNCIONARIO -> "funcionarios"
            Perfil.DIRETOR -> "diretores"
        }

        return try {
            val snapshot = db.collection(collectionName)
                .whereEqualTo("matricula", matricula)
                .whereEqualTo("senha", senha)
                .limit(1)
                .get()
                .await()

            if (snapshot.documents.size > 0) {
                val doc = snapshot.documents.first()
                val uid = doc.id
                val nome = doc.getString("nome") ?: "Usuário"
                Pair(uid, nome)
            } else {
                null
            }
        } catch (e: Exception) {
            println("Erro durante o login: ${e.message}")
            null
        }
    }

    fun getAllEscolas(): Flow<Resource<List<Escola>>> = db
        .collection("escolas")
        .orderBy("nome")
        .snapshots()
        .map { snapshot ->
            val escolas = snapshot.documents.mapNotNull { doc ->
                doc.toObject<Escola>()?.copy(id = doc.id)
            }
            Resource.Success(escolas) as Resource<List<Escola>>
        }
        .onStart { emit(Resource.Loading()) }
        .catch { e ->
            emit(Resource.Error(e.message))
        }

    fun getLoanHistory(userId: String): Flow<Resource<List<LoanHistory>>> = db
        .collection("alunos")
        .document(userId)
        .collection("historico_emprestimos")
        .orderBy("loanDate", Query.Direction.DESCENDING)
        .snapshots()
        .map { snapshot ->
            val history = snapshot.documents.mapNotNull { doc ->
                doc.toObject<LoanHistory>()
            }
            Resource.Success(history) as Resource<List<LoanHistory>>
        }
        .onStart { emit(Resource.Loading()) }
        .catch { e ->
            emit(Resource.Error(e.message))
        }

    suspend fun registerStudent(aluno: StudentData): Resource<String> {
        return try {
            val matriculaQuery = db.collection("alunos")
                .whereEqualTo("matricula", aluno.matricula)
                .limit(1)
                .get()
                .await()

            if (matriculaQuery.documents.size > 0) {
                return Resource.Error("Esta matrícula já está cadastrada.")
            }

            val cpfQuery = db.collection("alunos")
                .whereEqualTo("cpf", aluno.cpf)
                .limit(1)
                .get()
                .await()

            if (cpfQuery.documents.size > 0) {
                return Resource.Error("Este CPF já está cadastrado para outro aluno.")
            }

            val documentRef = db.collection("alunos")
                .add(aluno)
                .await()
            Resource.Success(documentRef.id)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro desconhecido ao cadastrar aluno.")
        }
    }

    fun getAllStudents(): Flow<Resource<List<StudentData>>> = db
        .collection("alunos")
        .orderBy("nome")
        .snapshots()
        .map { snapshot ->
            val students = snapshot.documents.mapNotNull { doc ->
                doc.toObject<StudentData>()?.copy(id = doc.id)
            }
            Resource.Success(students) as Resource<List<StudentData>>
        }
        .onStart { emit(Resource.Loading()) }
        .catch { e ->
            emit(Resource.Error(e.message))
        }

    fun getAllBikes(): Flow<Resource<List<Bike>>> = db
        .collection("bicicletas")
        .orderBy("numero")
        .snapshots()
        .map { snapshot ->
            val bikes = snapshot.documents.mapNotNull { doc ->
                doc.toObject<Bike>()?.copy(id = doc.id)
            }
            Resource.Success(bikes) as Resource<List<Bike>>
        }
        .onStart { emit(Resource.Loading()) }
        .catch { e ->
            emit(Resource.Error(e.message))
        }

    suspend fun lendBike(bike: Bike, studentMatricula: String): Resource<Boolean> {
        return try {
            val studentQuery = db.collection("alunos")
                .whereEqualTo("matricula", studentMatricula)
                .limit(1)
                .get()
                .await()

            if (studentQuery.isEmpty) {
                return Resource.Error("Matrícula de aluno não encontrada.")
            }

            val studentDoc = studentQuery.documents.first()
            val studentId = studentDoc.id
            val studentName = studentDoc.getString("nome") ?: "Nome não encontrado"
            val studentSchool = studentDoc.getString("escola") ?: "Escola não informada"

            db.runTransaction { transaction ->
                val bikeRef = db.collection("bicicletas").document(bike.id)
                val newHistoryRef = db.collection("alunos").document(studentId)
                    .collection("historico_emprestimos").document()

                transaction.update(bikeRef, mapOf(
                    "status" to "emprestada",
                    "alunoEmprestadoId" to studentId,
                    "alunoEmprestadoNome" to studentName,
                    "escolaNome" to studentSchool
                ))

                val newLoan = mapOf(
                    "bikeNumber" to bike.numero,
                    "loanDate" to FieldValue.serverTimestamp(),
                    "returnDate" to null
                )
                transaction.set(newHistoryRef, newLoan)

            }.await()

            Resource.Success(true)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro desconhecido na transação.")
        }
    }

    suspend fun returnBike(bike: Bike): Resource<Boolean> {
        val studentId = bike.alunoEmprestadoId
        if (studentId == null) {
            return Resource.Error("ID do aluno está nulo na bicicleta. Reinicie o app e tente novamente.")
        }

        return try {
            val historyQuery = db.collection("alunos").document(studentId)
                .collection("historico_emprestimos")
                .whereEqualTo("bikeNumber", bike.numero)
                .orderBy("loanDate", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            if (historyQuery.isEmpty) {
                return Resource.Error("Nenhum registro de histórico encontrado para esta bicicleta.")
            }

            val historyDoc = historyQuery.documents.first()

            if (historyDoc.getDate("returnDate") != null) {
                return Resource.Error("Esta bicicleta já consta como devolvida no histórico.")
            }

            db.runTransaction { transaction ->
                val bikeRef = db.collection("bicicletas").document(bike.id)
                val historyDocRef = historyDoc.reference

                transaction.update(bikeRef, mapOf(
                    "status" to "disponivel",
                    "alunoEmprestadoId" to null,
                    "alunoEmprestadoNome" to null,
                    "escolaNome" to null
                ))

                transaction.update(historyDocRef, "returnDate", FieldValue.serverTimestamp())
            }.await()

            Resource.Success(true)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro desconhecido na transação de devolução.")
        }
    }

    suspend fun registerEmployee(employee: EmployeeData): Resource<String> {
        return try {
            val matriculaQuery = db.collection("funcionarios")
                .whereEqualTo("matricula", employee.matricula)
                .limit(1)
                .get()
                .await()

            if (matriculaQuery.documents.size > 0) {
                return Resource.Error("Esta matrícula já está cadastrada para outro funcionário.")
            }

            val cpfQuery = db.collection("funcionarios")
                .whereEqualTo("cpf", employee.cpf)
                .limit(1)
                .get()
                .await()

            if (cpfQuery.documents.size > 0) {
                return Resource.Error("Este CPF já está cadastrado para outro funcionário.")
            }

            val documentRef = db.collection("funcionarios")
                .add(employee)
                .await()
            Resource.Success(documentRef.id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro desconhecido ao cadastrar funcionário.")
        }
    }

    suspend fun registerBike(bike: Bike): Resource<String> {
        return try {
            val numeroQuery = db.collection("bicicletas")
                .whereEqualTo("numero", bike.numero)
                .limit(1)
                .get()
                .await()

            if (numeroQuery.documents.size > 0) {
                return Resource.Error("Esta bicicleta já está cadastrada.")
            }

            val documentRef = db.collection("bicicletas")
                .add(bike)
                .await()
            Resource.Success(documentRef.id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro desconhecido ao cadastrar bicicleta.")
        }
    }
}