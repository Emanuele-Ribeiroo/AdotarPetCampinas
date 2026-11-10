package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adotarpet.campinas.model.HistoryEntry
import com.adotarpet.campinas.repository.AdoptionApiService
import com.adotarpet.campinas.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

sealed interface CancelState {
    data object Idle : CancelState
    data object Enviando : CancelState
    data object Concluido : CancelState
    data class Erro(val message: String) : CancelState
}

class RequestDetailViewModel : ViewModel() {

    val entries: StateFlow<List<HistoryEntry>> = HistoryRepository.entries

    private val _cancelState = MutableStateFlow<CancelState>(CancelState.Idle)
    val cancelState: StateFlow<CancelState> = _cancelState

    fun entryById(id: String): HistoryEntry? = entries.value.find { it.id == id }

    /** Campos do formulário, em ordem, com rótulo legível (só os preenchidos). */
    fun camposExibicao(entry: HistoryEntry): List<Pair<String, String>> {
        val dados = detalhesMap(entry.details)
        return LABELS.mapNotNull { (chave, rotulo) ->
            val valor = dados[chave]?.trim().orEmpty()
            if (valor.isBlank()) null else rotulo to valor
        }
    }

    fun cancelar(entry: HistoryEntry, motivo: String) {
        if (_cancelState.value == CancelState.Enviando) return
        _cancelState.value = CancelState.Enviando

        HistoryRepository.cancelar(entry.id, motivo)

        viewModelScope.launch {
            if (entry.details.isBlank()) {
                _cancelState.value = CancelState.Concluido
                return@launch
            }
            val dados = detalhesMap(entry.details).toMutableMap().apply {
                put("status", "Cancelada")
                put("motivoCancelamento", motivo)
            }
            _cancelState.value = AdoptionApiService.enviarFormulario(dados).fold(
                onSuccess = { CancelState.Concluido },
                onFailure = { CancelState.Erro(it.message ?: "Falha ao enviar para a planilha") }
            )
        }
    }

    private fun detalhesMap(json: String): Map<String, String> {
        if (json.isBlank()) return emptyMap()
        return runCatching {
            val o = JSONObject(json)
            val mapa = LinkedHashMap<String, String>()
            o.keys().forEach { chave -> mapa[chave] = o.optString(chave) }
            mapa
        }.getOrDefault(emptyMap())
    }

    companion object {
        private val LABELS = listOf(
            "animal" to "Animal",
            "dataPreenchimento" to "Data do preenchimento",
            "nomeCompleto" to "Nome completo",
            "idade" to "Idade",
            "cpf" to "CPF",
            "telefone" to "Telefone / WhatsApp",
            "email" to "E-mail",
            "instagram" to "Instagram",
            "profissao" to "Profissão",
            "localTrabalho" to "Local de trabalho",
            "enderecoTrabalho" to "Endereço do trabalho",
            "familiaConcorda" to "Família concorda com a adoção",
            "caracteristicasDesejadas" to "Características desejadas",
            "motivoAdocao" to "Motivo da adoção",
            "familiaZelaSaudeSeguranca" to "Família zela pela saúde e segurança",
            "haCriancas" to "Há crianças na casa",
            "quantasCriancas" to "Quantas crianças",
            "idadeCriancas" to "Idade das crianças",
            "criancasVisitamCasa" to "Crianças visitam a casa",
            "frequenciaVisitas" to "Frequência das visitas",
            "idadeVisitantes" to "Idade dos visitantes",
            "haIdosos" to "Há idosos na casa",
            "problemaRespiratorio" to "Alguém tem problema respiratório",
            "cep" to "CEP",
            "cidade" to "Cidade",
            "enderecoCompleto" to "Endereço completo",
            "tipoMoradia" to "Tipo de moradia",
            "tipoImovel" to "Tipo de imóvel",
            "alugadoPermiteAnimais" to "Imóvel alugado permite animais",
            "temMuroPortoes" to "Tem muro e portões",
            "apartamentoTemTelas" to "Apartamento tem telas de proteção",
            "planoSeRotinaMudar" to "Plano se a rotina mudar",
            "experienciaPets" to "Experiência com pets",
            "temOutroAnimal" to "Tem outro animal",
            "especie" to "Espécie do(s) animal(is)",
            "quantosAnimais" to "Quantos animais",
            "idadeAnimais" to "Idade dos animais",
            "animaisVacinados" to "Animais vacinados",
            "porte" to "Porte",
            "jaPerdeuAnimal" to "Já perdeu um animal",
            "motivoPerda" to "Motivo da perda",
            "rotina" to "Rotina",
            "quemCuidaViagem" to "Quem cuida em viagens",
            "reacaoSeDestruirBem" to "Reação se destruir um bem",
            "marcaRacao" to "Marca da ração",
            "condicoesFinanceiras" to "Tem condições financeiras",
            "aceitaVisitaVoluntario" to "Aceita visita de voluntário",
            "mantemContatoOng" to "Mantém contato com a ONG",
            "consentimentoLgpd" to "Consentimento LGPD"
        )
    }
}
