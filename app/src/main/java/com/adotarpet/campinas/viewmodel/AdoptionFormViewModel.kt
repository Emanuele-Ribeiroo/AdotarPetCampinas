package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.model.UserProfile
import com.adotarpet.campinas.repository.AdoptionApiService
import com.adotarpet.campinas.repository.CepResultado
import com.adotarpet.campinas.repository.CepService
import com.adotarpet.campinas.repository.HistoryRepository
import com.adotarpet.campinas.repository.PetRepository
import com.adotarpet.campinas.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

sealed interface SubmitState {
    data object Idle : SubmitState
    data object Loading : SubmitState
    data object Success : SubmitState
    data class Error(val message: String) : SubmitState
}

class AdoptionFormViewModel : ViewModel() {

    private val _state = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val state: StateFlow<SubmitState> = _state

    val perfil: UserProfile get() = UserRepository.profile.value

    fun petById(id: String): Pet? = PetRepository.petById(id)

    suspend fun buscarCep(cep: String): CepResultado = CepService.buscar(cep)

    fun enviar(dados: Map<String, String>, nome: String, cpf: String, telefone: String, email: String) {
        if (_state.value == SubmitState.Loading) return
        _state.value = SubmitState.Loading
        UserRepository.fillBlanks(nome, cpf, telefone, email)
        viewModelScope.launch {
            _state.value = AdoptionApiService.enviarFormulario(dados).fold(
                onSuccess = {
                    val detalhes = JSONObject().apply {
                        dados.forEach { (chave, valor) -> put(chave, valor) }
                    }.toString()
                    dados["idAnimal"]?.let { id ->
                        petById(id)?.let { pet ->
                            HistoryRepository.add(
                                HistoryType.ADOCAO,
                                "Adoção de ${pet.name}",
                                "${pet.speciesLabel} · ${pet.age}",
                                detalhes
                            )
                        }
                    }
                    SubmitState.Success
                },
                onFailure = { SubmitState.Error(it.message ?: "Falha ao enviar") }
            )
        }
    }
}
