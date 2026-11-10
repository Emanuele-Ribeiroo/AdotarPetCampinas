package com.adotarpet.campinas.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adotarpet.campinas.model.Attachment
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.model.UserProfile
import com.adotarpet.campinas.repository.AdoptionRepository
import com.adotarpet.campinas.repository.CepRepository
import com.adotarpet.campinas.repository.CepResultado
import com.adotarpet.campinas.repository.HistoryRepository
import com.adotarpet.campinas.repository.PetRepository
import com.adotarpet.campinas.repository.UserRepository
import com.adotarpet.campinas.util.Validators
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface SubmitState {
    data object Idle : SubmitState
    data object Loading : SubmitState
    data object Success : SubmitState
    data class Error(val message: String) : SubmitState
}

/**
 * Guarda todo o estado do formulário de adoção e as regras de validação.
 * A tela só lê estes valores e avisa o ViewModel dos eventos (digitação, cliques).
 */
class AdoptionFormViewModel(petId: String) : ViewModel() {

    val pet: Pet? = PetRepository.petById(petId)
    private val perfil: UserProfile = UserRepository.profile.value

    private val _state = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val state: StateFlow<SubmitState> = _state

    var step by mutableIntStateOf(1)
        private set

    // ---------- Etapa 1 · Dados pessoais ----------
    var data by mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date()))
    var nome by mutableStateOf(perfil.name)
    var idade by mutableStateOf("")
        private set
    var cpf by mutableStateOf(perfil.cpf)
        private set
    var email by mutableStateOf(perfil.email)
        private set
    var telefone by mutableStateOf(perfil.phone)
        private set
    var instagram by mutableStateOf("")
    var profissao by mutableStateOf("")
    var trabalhoNome by mutableStateOf("")
    var trabalhoEndereco by mutableStateOf("")
    var familiaConcorda by mutableStateOf<Boolean?>(null)
    var caracteristicas by mutableStateOf("")
    var motivo by mutableStateOf("")
    var membrosZelam by mutableStateOf<Boolean?>(null)
    var temCriancas by mutableStateOf<Boolean?>(null)
        private set
    var quantasCriancas by mutableStateOf("")
        private set
    var idadesCriancas by mutableStateOf("")
    var criancasVisitam by mutableStateOf<Boolean?>(null)
        private set
    var freqVisita by mutableStateOf("")
    var idadeVisitantes by mutableStateOf("")
    var idosos by mutableStateOf<Boolean?>(null)
    var problemaRespiratorio by mutableStateOf<Boolean?>(null)

    // ---------- Etapa 2 · Moradia ----------
    var cep by mutableStateOf("")
        private set
    var cidade by mutableStateOf(pet?.city.orEmpty())
    var endereco by mutableStateOf("")
    var cepBuscando by mutableStateOf(false)
        private set
    var cepApiInvalido by mutableStateOf(false)
        private set
    var moradia by mutableStateOf<String?>(null)
        private set
    var tipoImovel by mutableStateOf<String?>(null)
        private set
    var permitidoAnimais by mutableStateOf<Boolean?>(null)
    var muroPortoes by mutableStateOf<Boolean?>(null)
    var telasProtecao by mutableStateOf<Boolean?>(null)
    var situacaoRotina by mutableStateOf("")
    var anexosCasa by mutableStateOf<List<Attachment>>(emptyList())

    // ---------- Etapa 3 · Cuidados e convivência ----------
    var experiencia by mutableStateOf<String?>(null)
    var temOutrosAnimais by mutableStateOf<Boolean?>(null)
        private set
    var especieAnimais by mutableStateOf("")
    var quantosAnimais by mutableStateOf("")
        private set
    var idadeAnimais by mutableStateOf("")
    var vacinados by mutableStateOf<Boolean?>(null)
    var porteAnimais by mutableStateOf<String?>(null)
    var jaPerdeuAnimal by mutableStateOf<Boolean?>(null)
        private set
    var motivoPerda by mutableStateOf("")
    var cuidadorViagem by mutableStateOf("")
    var reacaoDestruicao by mutableStateOf("")
    var marcaRacao by mutableStateOf("")
    var condicoesFinanceiras by mutableStateOf<Boolean?>(null)
    var rotina by mutableStateOf("")
    var aceitaVisita by mutableStateOf<Boolean?>(null)
    var mantemContato by mutableStateOf<Boolean?>(null)
    var lgpdConsent by mutableStateOf<Boolean?>(null)

    // ---------- Validação ----------
    val cpfOk: Boolean get() = Validators.isValidCpf(cpf)
    val telefoneOk: Boolean get() = Validators.isValidPhone(telefone)
    val emailOk: Boolean get() = Validators.isValidEmail(email)
    val cepOk: Boolean get() = Validators.isValidCep(cep)

    private val criancasOk: Boolean
        get() = when (temCriancas) {
            true -> quantasCriancas.isNotBlank()
            false -> criancasVisitam != null && (criancasVisitam == false || freqVisita.isNotBlank())
            null -> false
        }

    private val step1Ok: Boolean
        get() = nome.isNotBlank() && cpfOk && emailOk && telefoneOk &&
            idade.isNotBlank() && profissao.isNotBlank() && caracteristicas.isNotBlank() &&
            familiaConcorda != null && membrosZelam != null && criancasOk &&
            idosos != null && problemaRespiratorio != null

    private val step2Ok: Boolean
        get() = cepOk && !cepApiInvalido && cidade.isNotBlank() && endereco.isNotBlank() &&
            moradia != null && tipoImovel != null && muroPortoes != null && situacaoRotina.isNotBlank() &&
            (tipoImovel != "Alugado" || permitidoAnimais != null) &&
            (moradia != "Apartamento" || telasProtecao != null)

    private val outrosAnimaisOk: Boolean
        get() = temOutrosAnimais == false ||
            (temOutrosAnimais == true && especieAnimais.isNotBlank() && quantosAnimais.isNotBlank() &&
                idadeAnimais.isNotBlank() && vacinados != null && porteAnimais != null)

    private val perdaOk: Boolean
        get() = jaPerdeuAnimal == false || (jaPerdeuAnimal == true && motivoPerda.isNotBlank())

    private val step3Ok: Boolean
        get() = experiencia != null && temOutrosAnimais != null && outrosAnimaisOk &&
            jaPerdeuAnimal != null && perdaOk && cuidadorViagem.isNotBlank() &&
            reacaoDestruicao.isNotBlank() && marcaRacao.isNotBlank() && condicoesFinanceiras != null &&
            rotina.isNotBlank() && aceitaVisita != null && mantemContato != null && lgpdConsent == true

    /** A etapa atual está completa e válida? */
    val podeAvancar: Boolean
        get() = when (step) {
            1 -> step1Ok
            2 -> step2Ok
            else -> step3Ok
        }

    /** O usuário já preencheu algo? Usado para confirmar antes de sair e descartar. */
    val temAlteracoes: Boolean
        get() = nome != perfil.name || cpf != perfil.cpf || telefone != perfil.phone ||
            email != perfil.email || idade.isNotBlank() || instagram.isNotBlank() || profissao.isNotBlank() ||
            trabalhoNome.isNotBlank() || trabalhoEndereco.isNotBlank() || familiaConcorda != null ||
            caracteristicas.isNotBlank() || motivo.isNotBlank() || membrosZelam != null || temCriancas != null ||
            idosos != null || problemaRespiratorio != null || cep.isNotBlank() || endereco.isNotBlank() ||
            moradia != null || tipoImovel != null || muroPortoes != null || situacaoRotina.isNotBlank() ||
            anexosCasa.isNotEmpty() || experiencia != null || temOutrosAnimais != null ||
            jaPerdeuAnimal != null || cuidadorViagem.isNotBlank() || reacaoDestruicao.isNotBlank() ||
            marcaRacao.isNotBlank() || condicoesFinanceiras != null || rotina.isNotBlank() ||
            aceitaVisita != null || mantemContato != null || lgpdConsent != null

    // ---------- Eventos da tela ----------
    fun onIdadeChange(texto: String) { idade = texto.filter { it.isDigit() }.take(3) }
    fun onCpfChange(texto: String) { cpf = Validators.onlyCpf(texto) }
    fun onTelefoneChange(texto: String) { telefone = Validators.onlyPhone(texto) }
    fun onEmailChange(texto: String) { email = texto.trim() }
    fun onQuantasCriancasChange(texto: String) { quantasCriancas = texto.filter { it.isDigit() }.take(2) }
    fun onQuantosAnimaisChange(texto: String) { quantosAnimais = texto.filter { it.isDigit() }.take(2) }

    fun onTemCriancasChange(valor: Boolean) {
        temCriancas = valor
        if (valor) {
            criancasVisitam = null; freqVisita = ""; idadeVisitantes = ""
        } else {
            quantasCriancas = ""; idadesCriancas = ""
        }
    }

    fun onCriancasVisitamChange(valor: Boolean) {
        criancasVisitam = valor
        if (!valor) { freqVisita = ""; idadeVisitantes = "" }
    }

    fun onMoradiaChange(valor: String) {
        moradia = valor
        if (valor != "Apartamento") telasProtecao = null
    }

    fun onTipoImovelChange(valor: String) {
        tipoImovel = valor
        if (valor != "Alugado") permitidoAnimais = null
    }

    fun onTemOutrosAnimaisChange(valor: Boolean) {
        temOutrosAnimais = valor
        if (!valor) {
            especieAnimais = ""; quantosAnimais = ""; idadeAnimais = ""
            vacinados = null; porteAnimais = null
        }
    }

    fun onJaPerdeuAnimalChange(valor: Boolean) {
        jaPerdeuAnimal = valor
        if (!valor) motivoPerda = ""
    }

    private var buscaCep: Job? = null

    /** Ao completar 8 dígitos, consulta o CEP e preenche cidade e endereço. */
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
                    if (r.endereco.cidade.isNotBlank()) cidade = r.endereco.cidade
                    if (endereco.isBlank()) {
                        endereco = listOf(r.endereco.logradouro, r.endereco.bairro)
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

    fun voltarEtapa() {
        if (step > 1) step--
    }

    /** Botão principal: vai para a próxima etapa ou, na última, envia a solicitação. */
    fun avancar() {
        if (!podeAvancar) return
        if (step < 3) step++ else enviar()
    }

    private fun enviar() {
        val pet = pet ?: return
        if (_state.value == SubmitState.Loading) return
        _state.value = SubmitState.Loading

        val dados = montarDados(pet)
        UserRepository.fillBlanks(nome.trim(), cpf, telefone, email)
        viewModelScope.launch {
            _state.value = AdoptionRepository.enviarFormulario(dados).fold(
                onSuccess = {
                    val detalhes = JSONObject().apply {
                        dados.forEach { (chave, valor) -> put(chave, valor) }
                    }.toString()
                    HistoryRepository.add(
                        HistoryType.ADOCAO,
                        "Adoção de ${pet.name}",
                        "${pet.speciesLabel} · ${pet.age}",
                        detalhes
                    )
                    SubmitState.Success
                },
                onFailure = { SubmitState.Error(it.message ?: "Falha ao enviar") }
            )
        }
    }

    private fun montarDados(pet: Pet): Map<String, String> = mapOf(
        "animal" to pet.name,
        "idAnimal" to pet.id,
        "dataPreenchimento" to data,
        "nomeCompleto" to nome,
        "idade" to idade,
        "cpf" to Validators.maskCpf(cpf),
        "telefone" to Validators.maskPhone(telefone),
        "email" to email,
        "instagram" to instagram,
        "profissao" to profissao,
        "localTrabalho" to trabalhoNome,
        "enderecoTrabalho" to trabalhoEndereco,
        "familiaConcorda" to sn(familiaConcorda),
        "caracteristicasDesejadas" to caracteristicas,
        "motivoAdocao" to motivo,
        "familiaZelaSaudeSeguranca" to sn(membrosZelam),
        "haCriancas" to sn(temCriancas),
        "quantasCriancas" to quantasCriancas,
        "idadeCriancas" to idadesCriancas,
        "criancasVisitamCasa" to sn(criancasVisitam),
        "frequenciaVisitas" to freqVisita,
        "idadeVisitantes" to idadeVisitantes,
        "haIdosos" to sn(idosos),
        "problemaRespiratorio" to sn(problemaRespiratorio),
        "cep" to Validators.maskCep(cep),
        "cidade" to cidade,
        "enderecoCompleto" to endereco,
        "tipoMoradia" to (moradia ?: ""),
        "tipoImovel" to (tipoImovel ?: ""),
        "alugadoPermiteAnimais" to sn(permitidoAnimais),
        "temMuroPortoes" to sn(muroPortoes),
        "apartamentoTemTelas" to sn(telasProtecao),
        "planoSeRotinaMudar" to situacaoRotina,
        "experienciaPets" to (experiencia ?: ""),
        "temOutroAnimal" to sn(temOutrosAnimais),
        "especie" to especieAnimais,
        "quantosAnimais" to quantosAnimais,
        "idadeAnimais" to idadeAnimais,
        "animaisVacinados" to sn(vacinados),
        "porte" to (porteAnimais ?: ""),
        "jaPerdeuAnimal" to sn(jaPerdeuAnimal),
        "motivoPerda" to motivoPerda,
        "rotina" to rotina,
        "quemCuidaViagem" to cuidadorViagem,
        "reacaoSeDestruirBem" to reacaoDestruicao,
        "marcaRacao" to marcaRacao,
        "condicoesFinanceiras" to sn(condicoesFinanceiras),
        "aceitaVisitaVoluntario" to sn(aceitaVisita),
        "mantemContatoOng" to sn(mantemContato),
        "consentimentoLgpd" to sn(lgpdConsent)
    )

    private fun sn(b: Boolean?): String = when (b) {
        true -> "Sim"
        false -> "Não"
        null -> ""
    }
}
