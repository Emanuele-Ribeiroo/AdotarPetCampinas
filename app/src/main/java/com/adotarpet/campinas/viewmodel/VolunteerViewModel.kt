package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.model.UserProfile
import com.adotarpet.campinas.repository.HistoryRepository
import com.adotarpet.campinas.repository.UserRepository

class VolunteerViewModel : ViewModel() {
    val perfil: UserProfile get() = UserRepository.profile.value

    fun enviarCadastro(nome: String, telefone: String, email: String, areas: String) {
        UserRepository.fillBlanks(name = nome, phone = telefone, email = email)
        HistoryRepository.add(HistoryType.VOLUNTARIO, "Cadastro de voluntário", areas)
    }
}
