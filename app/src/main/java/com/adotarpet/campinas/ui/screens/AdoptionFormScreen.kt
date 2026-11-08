package com.adotarpet.campinas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.util.Validators
import com.adotarpet.campinas.data.HistoryRepository
import com.adotarpet.campinas.data.PetRepository
import com.adotarpet.campinas.data.UserRepository
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.ui.components.BackButton
import com.adotarpet.campinas.ui.components.rememberDiscardGuard
import com.adotarpet.campinas.ui.components.Attachment
import com.adotarpet.campinas.ui.components.AttachmentPicker
import com.adotarpet.campinas.ui.components.FilterChip
import com.adotarpet.campinas.ui.components.FormSectionLabel
import com.adotarpet.campinas.ui.components.FormTextField
import com.adotarpet.campinas.ui.theme.CreamCard
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TerracottaDark
import com.adotarpet.campinas.ui.theme.TerracottaLight
import com.adotarpet.campinas.ui.theme.TextSecondary
import com.adotarpet.campinas.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val moradiaOptions = listOf("Casa", "Apartamento", "Sítio/Chácara", "Outro")
private val imovelOptions = listOf("Próprio", "Alugado", "Cedido")
private val experienciaOptions = listOf("Primeira vez", "Já tive pets", "Tenho pets")
private val porteOptions = listOf("Pequeno", "Médio", "Grande")

/** Converte a resposta de Sim/Não (Boolean?) em texto para enviar ao backend. */
private fun sn(b: Boolean?): String = when (b) {
    true -> "Sim"
    false -> "Não"
    null -> ""
}

/** Pergunta de Sim/Não usada nas três etapas do formulário. */
@Composable
private fun YesNoQuestion(question: String, value: Boolean?, onChange: (Boolean) -> Unit) {
    Text(question, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FilterChip("Sim", selected = value == true, onClick = { onChange(true) })
        FilterChip("Não", selected = value == false, onClick = { onChange(false) })
    }
    Spacer(Modifier.height(16.dp))
}

/** Pergunta de escolha única (chips) usada nas etapas. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChoiceQuestion(
    question: String,
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit
) {
    Text(question, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        options.forEach { option ->
            FilterChip(option, selected = selected == option, onClick = { onSelect(option) })
        }
    }
    Spacer(Modifier.height(16.dp))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdoptionFormScreen(
    petId: String,
    onEnviarClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val pet = PetRepository.petById(petId) ?: return
    val perfil = remember { UserRepository.profile.value }
    val hoje = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date()) }

    val formViewModel: AdoptionFormViewModel = viewModel()
    val submitState by formViewModel.state.collectAsState()

    var step by remember { mutableIntStateOf(1) }

    // --- Etapa 1: dados pessoais e família ---
    var data by remember { mutableStateOf(hoje) }
    var nome by remember { mutableStateOf(perfil.name) }
    var idade by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf(perfil.cpf) }
    var email by remember { mutableStateOf(perfil.email) }
    var telefone by remember { mutableStateOf(perfil.phone) }
    var instagram by remember { mutableStateOf("") }
    var profissao by remember { mutableStateOf("") }
    var trabalhoNome by remember { mutableStateOf("") }
    var trabalhoEndereco by remember { mutableStateOf("") }
    var familiaConcorda by remember { mutableStateOf<Boolean?>(null) }
    var caracteristicas by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    var membrosZelam by remember { mutableStateOf<Boolean?>(null) }
    var temCriancas by remember { mutableStateOf<Boolean?>(null) }
    var quantasCriancas by remember { mutableStateOf("") }
    var idadesCriancas by remember { mutableStateOf("") }
    var criancasVisitam by remember { mutableStateOf<Boolean?>(null) }
    var freqVisita by remember { mutableStateOf("") }
    var idadeVisitantes by remember { mutableStateOf("") }
    var idosos by remember { mutableStateOf<Boolean?>(null) }
    var problemaRespiratorio by remember { mutableStateOf<Boolean?>(null) }

    // --- Etapa 2: moradia ---
    var cep by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf(pet.city) }
    var endereco by remember { mutableStateOf("") }
    var moradia by remember { mutableStateOf<String?>(null) }
    var tipoImovel by remember { mutableStateOf<String?>(null) }
    var permitidoAnimais by remember { mutableStateOf<Boolean?>(null) }
    var muroPortoes by remember { mutableStateOf<Boolean?>(null) }
    var telasProtecao by remember { mutableStateOf<Boolean?>(null) }
    var situacaoRotina by remember { mutableStateOf("") }
    var anexosCasa by remember { mutableStateOf<List<Attachment>>(emptyList()) }

    // --- Etapa 3: cuidados e convivência ---
    var experiencia by remember { mutableStateOf<String?>(null) }
    var temOutrosAnimais by remember { mutableStateOf<Boolean?>(null) }
    var especieAnimais by remember { mutableStateOf("") }
    var quantosAnimais by remember { mutableStateOf("") }
    var idadeAnimais by remember { mutableStateOf("") }
    var vacinados by remember { mutableStateOf<Boolean?>(null) }
    var porteAnimais by remember { mutableStateOf<String?>(null) }
    var jaPerdeuAnimal by remember { mutableStateOf<Boolean?>(null) }
    var motivoPerda by remember { mutableStateOf("") }
    var cuidadorViagem by remember { mutableStateOf("") }
    var reacaoDestruicao by remember { mutableStateOf("") }
    var marcaRacao by remember { mutableStateOf("") }
    var condicoesFinanceiras by remember { mutableStateOf<Boolean?>(null) }
    var rotina by remember { mutableStateOf("") }
    var aceitaVisita by remember { mutableStateOf<Boolean?>(null) }
    var mantemContato by remember { mutableStateOf<Boolean?>(null) }
    var lgpdConsent by remember { mutableStateOf<Boolean?>(null) }

    val cpfOk = Validators.isValidCpf(cpf)
    val telefoneOk = Validators.isValidPhone(telefone)
    val emailOk = Validators.isValidEmail(email)
    val cepOk = Validators.isValidCep(cep)

    val criancasOk = when (temCriancas) {
        true -> quantasCriancas.isNotBlank()
        false -> criancasVisitam != null && (criancasVisitam == false || freqVisita.isNotBlank())
        null -> false
    }
    val step1Ok = nome.isNotBlank() && cpfOk && emailOk && telefoneOk &&
        idade.isNotBlank() && profissao.isNotBlank() && caracteristicas.isNotBlank() &&
        familiaConcorda != null && membrosZelam != null && criancasOk &&
        idosos != null && problemaRespiratorio != null

    val outrosAnimaisOk = temOutrosAnimais == false ||
        (temOutrosAnimais == true && especieAnimais.isNotBlank() && quantosAnimais.isNotBlank() &&
            idadeAnimais.isNotBlank() && vacinados != null && porteAnimais != null)
    val perdaOk = jaPerdeuAnimal == false || (jaPerdeuAnimal == true && motivoPerda.isNotBlank())

    val step2Ok = cepOk && cidade.isNotBlank() && endereco.isNotBlank() && moradia != null &&
        tipoImovel != null && muroPortoes != null && situacaoRotina.isNotBlank() &&
        (tipoImovel != "Alugado" || permitidoAnimais != null) &&
        (moradia != "Apartamento" || telasProtecao != null)

    val step3Ok = experiencia != null && temOutrosAnimais != null && outrosAnimaisOk &&
        jaPerdeuAnimal != null && perdaOk && cuidadorViagem.isNotBlank() &&
        reacaoDestruicao.isNotBlank() && marcaRacao.isNotBlank() && condicoesFinanceiras != null &&
        rotina.isNotBlank() && aceitaVisita != null && mantemContato != null && lgpdConsent == true

    val anyChanges = nome != perfil.name || cpf != perfil.cpf || telefone != perfil.phone ||
        email != perfil.email || idade.isNotBlank() || instagram.isNotBlank() || profissao.isNotBlank() ||
        trabalhoNome.isNotBlank() || trabalhoEndereco.isNotBlank() || familiaConcorda != null ||
        caracteristicas.isNotBlank() || motivo.isNotBlank() || membrosZelam != null || temCriancas != null ||
        idosos != null || problemaRespiratorio != null || cep.isNotBlank() || endereco.isNotBlank() ||
        moradia != null || tipoImovel != null || muroPortoes != null || situacaoRotina.isNotBlank() ||
        anexosCasa.isNotEmpty() || experiencia != null || temOutrosAnimais != null ||
        jaPerdeuAnimal != null || cuidadorViagem.isNotBlank() || reacaoDestruicao.isNotBlank() ||
        marcaRacao.isNotBlank() || condicoesFinanceiras != null || rotina.isNotBlank() ||
        aceitaVisita != null || mantemContato != null || lgpdConsent != null

    val tentarVoltar = rememberDiscardGuard(hasChanges = anyChanges, onDiscard = onBackClick)

    // Só registra o histórico e avança quando o backend confirma o recebimento.
    LaunchedEffect(submitState) {
        if (submitState is SubmitState.Success) {
            HistoryRepository.add(HistoryType.ADOCAO, "Adoção de ${pet.name}", "${pet.speciesLabel} · ${pet.age}")
            onEnviarClick(pet.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        BackButton(onClick = { if (step > 1) step-- else tentarVoltar() })
        Text("Adoção · ${pet.name}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text("Formulário de adoção", style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)

        Spacer(Modifier.height(12.dp))
        val stepTitle = when (step) {
            1 -> "Etapa 1 de 3 · Dados pessoais"
            2 -> "Etapa 2 de 3 · Moradia"
            else -> "Etapa 3 de 3 · Cuidados e convivência"
        }
        Text(stepTitle, style = MaterialTheme.typography.bodyMedium, color = Terracotta)
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { step / 3f },
            modifier = Modifier.fillMaxWidth(),
            color = Terracotta,
            trackColor = CreamCard
        )

        Spacer(Modifier.height(24.dp))

        when (step) {
            1 -> {
                FormSectionLabel("DADOS PESSOAIS")
                FormTextField("Data", "dd/mm/aaaa", data) { data = it }
                FormTextField("Nome completo", "Como você se chama?", nome) { nome = it }
                FormTextField(
                    "Idade", "Ex.: 32", idade,
                    keyboardType = KeyboardType.Number
                ) { idade = it.filter { c -> c.isDigit() }.take(3) }
                FormTextField(
                    "CPF", "000.000.000-00", cpf,
                    keyboardType = KeyboardType.Number,
                    errorText = if (cpf.isNotEmpty() && !cpfOk) "CPF inválido" else null,
                    visualTransformation = Validators.cpfTransformation
                ) { cpf = Validators.onlyCpf(it) }

                Spacer(Modifier.height(8.dp))
                FormSectionLabel("CONTATO")
                FormTextField(
                    "Telefone / WhatsApp", "(19) 90000-0000", telefone,
                    keyboardType = KeyboardType.Phone,
                    errorText = if (telefone.isNotEmpty() && !telefoneOk) "Telefone inválido" else null,
                    visualTransformation = Validators.phoneTransformation
                ) { telefone = Validators.onlyPhone(it) }
                FormTextField(
                    "E-mail", "voce@email.com", email,
                    keyboardType = KeyboardType.Email,
                    errorText = if (email.isNotEmpty() && !emailOk) "E-mail inválido" else null
                ) { email = it.trim() }
                FormTextField("Instagram (opcional)", "@seuperfil", instagram) { instagram = it }

                Spacer(Modifier.height(8.dp))
                FormSectionLabel("TRABALHO")
                FormTextField("Profissão", "Sua profissão", profissao) { profissao = it }
                FormTextField("Local de trabalho (opcional)", "Nome da empresa", trabalhoNome) { trabalhoNome = it }
                FormTextField("Endereço do trabalho (opcional)", "Rua, número, bairro", trabalhoEndereco) { trabalhoEndereco = it }

                Spacer(Modifier.height(8.dp))
                FormSectionLabel("FAMÍLIA")
                YesNoQuestion("Todos da família concordam com a adoção?", familiaConcorda) { familiaConcorda = it }
                FormTextField(
                    "Características que deseja no animal", "Porte, temperamento, idade, etc.", caracteristicas,
                    singleLine = false
                ) { caracteristicas = it }
                FormTextField(
                    "Motivo da adoção (opcional)", "Por que você quer adotar este animal?", motivo,
                    singleLine = false
                ) { motivo = it }
                YesNoQuestion("Todos os membros zelarão pela saúde e segurança do animal?", membrosZelam) { membrosZelam = it }
                YesNoQuestion("Há crianças na residência?", temCriancas) {
                    temCriancas = it
                    if (it) { criancasVisitam = null; freqVisita = ""; idadeVisitantes = "" }
                    else { quantasCriancas = ""; idadesCriancas = "" }
                }
                if (temCriancas == true) {
                    FormTextField("Quantas crianças?", "Ex.: 2", quantasCriancas, keyboardType = KeyboardType.Number) {
                        quantasCriancas = it.filter { c -> c.isDigit() }.take(2)
                    }
                    FormTextField("Idade das crianças", "Ex.: 5 e 9 anos", idadesCriancas) { idadesCriancas = it }
                }
                if (temCriancas == false) {
                    YesNoQuestion("Crianças costumam visitar sua casa?", criancasVisitam) {
                        criancasVisitam = it
                        if (!it) { freqVisita = ""; idadeVisitantes = "" }
                    }
                    if (criancasVisitam == true) {
                        FormTextField("Com que frequência?", "Ex.: fins de semana", freqVisita) { freqVisita = it }
                        FormTextField("Idade dessas crianças", "Ex.: 3 e 7 anos", idadeVisitantes) { idadeVisitantes = it }
                    }
                }
                YesNoQuestion("Há idosos na residência?", idosos) { idosos = it }
                YesNoQuestion("Algum membro que terá contato com o animal tem problema respiratório?", problemaRespiratorio) { problemaRespiratorio = it }
            }

            2 -> {
                FormSectionLabel("ONDE O ANIMAL VAI MORAR")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormTextField(
                        "CEP", "13000-000", cep,
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        errorText = if (cep.isNotEmpty() && !cepOk) "CEP inválido" else null,
                        visualTransformation = Validators.cepTransformation
                    ) { cep = Validators.onlyCep(it) }
                    FormTextField("Cidade", "Campinas", cidade, modifier = Modifier.weight(1f)) { cidade = it }
                }
                FormTextField("Endereço completo", "Rua, número, bairro", endereco) { endereco = it }

                Spacer(Modifier.height(8.dp))
                FormSectionLabel("SOBRE A MORADIA")
                ChoiceQuestion("Tipo de moradia", moradiaOptions, moradia) {
                    moradia = it
                    if (it != "Apartamento") telasProtecao = null
                }
                ChoiceQuestion("O imóvel é:", imovelOptions, tipoImovel) {
                    tipoImovel = it
                    if (it != "Alugado") permitidoAnimais = null
                }
                if (tipoImovel == "Alugado") {
                    YesNoQuestion("No imóvel alugado é permitido ter animais?", permitidoAnimais) { permitidoAnimais = it }
                }
                YesNoQuestion("A casa tem muro ou portões para evitar fuga?", muroPortoes) { muroPortoes = it }
                if (moradia == "Apartamento") {
                    YesNoQuestion("O apartamento tem telas de proteção?", telasProtecao) { telasProtecao = it }
                }
                FormTextField(
                    "Se algo alterar a rotina da família, o que será feito com o animal?",
                    "Descreva o que faria", situacaoRotina,
                    singleLine = false
                ) { situacaoRotina = it }

                Spacer(Modifier.height(8.dp))
                AttachmentPicker(
                    label = "Fotos e vídeo de onde o animal vai morar",
                    helperText = "Ajuda a equipe a avaliar o espaço onde o animal vai viver",
                    attachments = anexosCasa,
                    onAttachmentsChange = { anexosCasa = it }
                )
            }

            else -> {
                FormSectionLabel("OUTROS ANIMAIS")
                ChoiceQuestion("Experiência com pets", experienciaOptions, experiencia) { experiencia = it }
                YesNoQuestion("Tem outro animal atualmente?", temOutrosAnimais) {
                    temOutrosAnimais = it
                    if (!it) {
                        especieAnimais = ""; quantosAnimais = ""; idadeAnimais = ""
                        vacinados = null; porteAnimais = null
                    }
                }
                if (temOutrosAnimais == true) {
                    FormTextField("Qual espécie?", "Ex.: cachorro, gato", especieAnimais) { especieAnimais = it }
                    FormTextField("Quantos são?", "Ex.: 2", quantosAnimais, keyboardType = KeyboardType.Number) {
                        quantosAnimais = it.filter { c -> c.isDigit() }.take(2)
                    }
                    FormTextField("Qual a idade deles?", "Ex.: 3 e 5 anos", idadeAnimais) { idadeAnimais = it }
                    YesNoQuestion("Todos são vacinados?", vacinados) { vacinados = it }
                    ChoiceQuestion("Qual o porte deles?", porteOptions, porteAnimais) { porteAnimais = it }
                }
                YesNoQuestion("Já perdeu algum animal?", jaPerdeuAnimal) {
                    jaPerdeuAnimal = it
                    if (!it) motivoPerda = ""
                }
                if (jaPerdeuAnimal == true) {
                    FormTextField("Qual foi o motivo?", "Conte o que aconteceu", motivoPerda, singleLine = false) { motivoPerda = it }
                }

                Spacer(Modifier.height(8.dp))
                FormSectionLabel("CUIDADOS")
                FormTextField(
                    "Rotina", "Quanto tempo o animal ficará sozinho? Quem vai cuidar dele?", rotina,
                    singleLine = false
                ) { rotina = it }
                FormTextField("Em caso de viagem, quem cuidaria do animal?", "Descreva", cuidadorViagem, singleLine = false) { cuidadorViagem = it }
                FormTextField("Se o animal destruir algum bem seu, o que você fará?", "Descreva", reacaoDestruicao, singleLine = false) { reacaoDestruicao = it }
                FormTextField("Qual marca de ração costuma usar?", "Ex.: marca X", marcaRacao) { marcaRacao = it }
                YesNoQuestion("Em caso de imprevisto, terá condições financeiras de atender o animal?", condicoesFinanceiras) { condicoesFinanceiras = it }

                Spacer(Modifier.height(8.dp))
                FormSectionLabel("ACOMPANHAMENTO")
                YesNoQuestion("Concorda que um voluntário visite sua casa para conhecer o local e as pessoas?", aceitaVisita) { aceitaVisita = it }
                YesNoQuestion("Concorda em manter contato com a ONG por tempo indeterminado para informar como está o animal?", mantemContato) { mantemContato = it }

                Spacer(Modifier.height(8.dp))
                FormSectionLabel("AUTORIZAÇÃO")
                YesNoQuestion(
                    "Autorizo o tratamento dos meus dados pessoais para fins deste processo de adoção, conforme a LGPD.",
                    lgpdConsent
                ) { lgpdConsent = it }
            }
        }

        Spacer(Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CreamCard)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(TerracottaLight, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Pets, contentDescription = null, tint = TerracottaDark)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Solicitação para ${pet.name}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${pet.speciesLabel} · ${pet.age}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))
        val podeAvancar = when (step) {
            1 -> step1Ok
            2 -> step2Ok
            else -> step3Ok
        }
        val enviando = submitState is SubmitState.Loading
        Button(
            onClick = {
                if (step < 3) {
                    step++
                } else {
                    UserRepository.fillBlanks(nome.trim(), cpf, telefone, email)
                    val dados = mapOf(
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
                    formViewModel.enviar(dados)
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White),
            enabled = podeAvancar && !enviando
        ) {
            if (enviando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(if (step < 3) "Continuar" else "Enviar solicitação")
            }
        }

        (submitState as? SubmitState.Error)?.let { erro ->
            Spacer(Modifier.height(12.dp))
            Text(
                "Não foi possível enviar: ${erro.message}. Verifique sua conexão e tente novamente.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}
