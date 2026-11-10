package com.adotarpet.campinas.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.model.UserProfile
import com.adotarpet.campinas.repository.UserRepository
import com.adotarpet.campinas.util.Validators

/** Estado e regras da tela "Meus dados". */
class MyDataViewModel : ViewModel() {

    private val salvo: UserProfile = UserRepository.profile.value

    var nome by mutableStateOf(salvo.name)
    var cpf by mutableStateOf(salvo.cpf)
        private set
    var telefone by mutableStateOf(salvo.phone)
        private set
    var email by mutableStateOf(salvo.email)
        private set

    // Campos são opcionais: vazio é aceito, preenchido precisa ser válido.
    val cpfOk: Boolean get() = cpf.isEmpty() || Validators.isValidCpf(cpf)
    val telefoneOk: Boolean get() = telefone.isEmpty() || Validators.isValidPhone(telefone)
    val emailOk: Boolean get() = email.isEmpty() || Validators.isValidEmail(email)

    val temAlteracoes: Boolean
        get() = nome != salvo.name || cpf != salvo.cpf || telefone != salvo.phone || email != salvo.email

    val podeSalvar: Boolean get() = temAlteracoes && cpfOk && telefoneOk && emailOk

    fun onCpfChange(texto: String) { cpf = Validators.onlyCpf(texto) }
    fun onTelefoneChange(texto: String) { telefone = Validators.onlyPhone(texto) }
    fun onEmailChange(texto: String) { email = texto.trim() }

    fun salvar() {
        if (!podeSalvar) return
        UserRepository.save(UserProfile(nome.trim(), cpf, telefone, email))
    }
}
