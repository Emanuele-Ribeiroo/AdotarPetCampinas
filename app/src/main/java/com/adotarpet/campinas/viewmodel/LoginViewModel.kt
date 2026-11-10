package com.adotarpet.campinas.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.repository.AuthRepository
import com.adotarpet.campinas.repository.UserRepository

/** Estado e regras da tela de login. */
class LoginViewModel : ViewModel() {

    var email by mutableStateOf("")
        private set
    var senha by mutableStateOf("")
        private set
    var mostrarSenha by mutableStateOf(false)
        private set
    var erro by mutableStateOf(false)
        private set

    val podeEntrar: Boolean get() = email.isNotBlank() && senha.isNotBlank()

    fun onEmailChange(texto: String) {
        email = texto.trim()
        erro = false
    }

    fun onSenhaChange(texto: String) {
        senha = texto
        erro = false
    }

    fun alternarMostrarSenha() {
        mostrarSenha = !mostrarSenha
    }

    /** O login é opcional: entra como visitante, sem e-mail nem senha. */
    fun entrarComoVisitante() {
        AuthRepository.loginAsGuest()
        email = ""
        senha = ""
        mostrarSenha = false
        erro = false
    }

    /**
     * Tenta entrar. Dando certo, o estado de "logado" muda no repositório
     * e a MainActivity troca sozinha para as telas do app.
     */
    fun entrar() {
        if (AuthRepository.login(email, senha)) {
            // Aproveita o e-mail da conta para pré-preencher os formulários.
            UserRepository.fillBlanks(email = email.trim().lowercase())
            // Este ViewModel vive enquanto a Activity viver: não deixa a senha guardada para depois do logout.
            email = ""
            senha = ""
            mostrarSenha = false
        } else {
            erro = true
        }
    }
}
