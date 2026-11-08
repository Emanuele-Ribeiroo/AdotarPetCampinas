package com.adotarpet.campinas.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adotarpet.campinas.data.AdoptionApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface SubmitState {
    data object Idle : SubmitState
    data object Loading : SubmitState
    data object Success : SubmitState
    data class Error(val message: String) : SubmitState
}

/** Controla o envio do formulário para o backend (Apps Script) e expõe o estado para a tela. */
class AdoptionFormViewModel : ViewModel() {

    private val _state = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val state: StateFlow<SubmitState> = _state

    fun enviar(dados: Map<String, String>) {
        if (_state.value == SubmitState.Loading) return
        _state.value = SubmitState.Loading
        viewModelScope.launch {
            _state.value = AdoptionApiService.enviarFormulario(dados).fold(
                onSuccess = { SubmitState.Success },
                onFailure = { SubmitState.Error(it.message ?: "Falha ao enviar") }
            )
        }
    }
}
