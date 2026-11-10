package com.adotarpet.campinas.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adotarpet.campinas.model.Attachment
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.repository.CepRepository
import com.adotarpet.campinas.repository.CepResultado
import com.adotarpet.campinas.repository.HistoryRepository
import com.adotarpet.campinas.repository.UserRepository
import com.adotarpet.campinas.util.Validators
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** Estado e regras do aviso de resgate. */
class RescueViewModel : ViewModel() {

    val animalOptions = listOf("Cão", "Gato", "Outro")
    val situacaoOptions = listOf("Ferido", "Abandonado", "Maus-tratos", "Preso")

    var animalSelecionado by mutableStateOf(animalOptions.first())
    var situacaoSelecionada by mutableStateOf(situacaoOptions.first())
    var endereco by mutableStateOf("")
    var cep by mutableStateOf("")
        private set
    var telefone by mutableStateOf(UserRepository.profile.value.phone)
        private set
    var anexos by mutableStateOf<List<Attachment>>(emptyList())
    var enviado by mutableStateOf(false)
        private set
    var cepBuscando by mutableStateOf(false)
        private set
    var cepApiInvalido by mutableStateOf(false)
        private set

    val telefoneOk: Boolean get() = Validators.isValidPhone(telefone)
    val cepInvalido: Boolean get() = cep.isNotEmpty() && (!Validators.isValidCep(cep) || cepApiInvalido)
    val podeEnviar: Boolean get() = endereco.isNotBlank() && telefoneOk && !cepInvalido

    fun onTelefoneChange(texto: String) { telefone = Validators.onlyPhone(texto) }

    private var buscaCep: Job? = null

    /** Ao completar 8 dígitos, consulta o CEP e sugere o endereço. */
    fun onCepChange(texto: String) {
        val novo = Validators.onlyCep(texto)
        if (novo == cep) return
        cep = novo
        cepApiInvalido = false
        buscaCep?.cancel()
        cepBuscando = false
        if (novo.length != 8) return

        cepBuscando = true
        buscaCep = viewModelScope.launch {
            when (val r = CepRepository.buscar(novo)) {
                is CepResultado.Encontrado -> {
                    if (endereco.isBlank()) {
                        endereco = listOf(r.endereco.logradouro, r.endereco.bairro, r.endereco.cidade)
                            .filter { it.isNotBlank() }
                            .joinToString(", ")
                    }
                }
                CepResultado.Invalido -> cepApiInvalido = true
                CepResultado.ErroRede -> {}
            }
            cepBuscando = false
        }
    }

    fun enviarResgate() {
        if (!podeEnviar) return
        UserRepository.fillBlanks(phone = telefone)
        HistoryRepository.add(HistoryType.RESGATE, "$animalSelecionado · $situacaoSelecionada", endereco.trim())
        enviado = true
    }

    /** Limpa o formulário para um novo aviso. */
    fun novoAviso() {
        buscaCep?.cancel()
        animalSelecionado = animalOptions.first()
        situacaoSelecionada = situacaoOptions.first()
        endereco = ""
        cep = ""
        cepBuscando = false
        cepApiInvalido = false
        telefone = UserRepository.profile.value.phone
        anexos = emptyList()
        enviado = false
    }
}
