package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.repository.CepResultado
import com.adotarpet.campinas.repository.CepService
import com.adotarpet.campinas.repository.HistoryRepository
import com.adotarpet.campinas.repository.UserRepository

class RescueViewModel : ViewModel() {
    val telefoneSalvo: String get() = UserRepository.profile.value.phone

    suspend fun buscarCep(cep: String): CepResultado = CepService.buscar(cep)

    fun enviarResgate(animal: String, situacao: String, endereco: String, telefone: String) {
        UserRepository.fillBlanks(phone = telefone)
        HistoryRepository.add(HistoryType.RESGATE, "$animal · $situacao", endereco.trim())
    }
}
