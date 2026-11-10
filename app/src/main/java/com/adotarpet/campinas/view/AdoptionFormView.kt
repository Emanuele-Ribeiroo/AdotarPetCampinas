package com.adotarpet.campinas.view

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.util.Validators
import com.adotarpet.campinas.viewmodel.AdoptionFormViewModel
import com.adotarpet.campinas.viewmodel.SubmitState
import com.adotarpet.campinas.view.components.BackButton
import com.adotarpet.campinas.view.components.rememberDiscardGuard
import com.adotarpet.campinas.view.components.AttachmentPicker
import com.adotarpet.campinas.view.components.FilterChip
import com.adotarpet.campinas.view.components.FormSectionLabel
import com.adotarpet.campinas.view.components.FormTextField
import com.adotarpet.campinas.view.theme.CreamCard
import com.adotarpet.campinas.view.theme.Terracotta
import com.adotarpet.campinas.view.theme.TerracottaDark
import com.adotarpet.campinas.view.theme.TerracottaLight
import com.adotarpet.campinas.view.theme.TextSecondary
import com.adotarpet.campinas.view.theme.White

private val moradiaOptions = listOf("Casa", "Apartamento", "Sítio/Chácara", "Outro")
private val imovelOptions = listOf("Próprio", "Alugado", "Cedido")
private val experienciaOptions = listOf("Primeira vez", "Já tive pets", "Tenho pets")
private val porteOptions = listOf("Pequeno", "Médio", "Grande")

@Composable
private fun YesNoQuestion(question: String, value: Boolean?, onChange: (Boolean) -> Unit) {
    Text(question, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FilterChip("Sim", selected = value == true, onClick = { onChange(true) })
        FilterChip("Não", selected = value == false, onClick = { onChange(false) })
    }
    Spacer(Modifier.height(16.dp))
}

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
fun AdoptionFormView(
    petId: String,
    onEnviarClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    // Todo o estado e as regras do formulário ficam no ViewModel; a tela só desenha e dispara eventos.
    val vm: AdoptionFormViewModel = viewModel { AdoptionFormViewModel(petId) }
    val submitState by vm.state.collectAsState()
    val pet = vm.pet ?: return

    val tentarVoltar = rememberDiscardGuard(hasChanges = vm.temAlteracoes, onDiscard = onBackClick)

    LaunchedEffect(submitState) {
        if (submitState is SubmitState.Success) {
            onEnviarClick(pet.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        BackButton(onClick = { if (vm.step > 1) vm.voltarEtapa() else tentarVoltar() })
        Text("Adoção · ${pet.name}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text("Formulário de adoção", style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)

        Spacer(Modifier.height(12.dp))
        val stepTitle = when (vm.step) {
            1 -> "Etapa 1 de 3 · Dados pessoais"
            2 -> "Etapa 2 de 3 · Moradia"
            else -> "Etapa 3 de 3 · Cuidados e convivência"
        }
        Text(stepTitle, style = MaterialTheme.typography.bodyMedium, color = Terracotta)
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { vm.step / 3f },
            modifier = Modifier.fillMaxWidth(),
            color = Terracotta,
            trackColor = CreamCard
        )

        Spacer(Modifier.height(24.dp))

        when (vm.step) {
            1 -> {
                FormSectionLabel("DADOS PESSOAIS")
                FormTextField("Data", "dd/mm/aaaa", vm.data) { vm.data = it }
                FormTextField("Nome completo", "Como você se chama?", vm.nome) { vm.nome = it }
                FormTextField(
                    "Idade", "Ex.: 32", vm.idade,
                    keyboardType = KeyboardType.Number
                ) { vm.onIdadeChange(it) }
                FormTextField(
                    "CPF", "000.000.000-00", vm.cpf,
                    keyboardType = KeyboardType.Number,
                    errorText = if (vm.cpf.isNotEmpty() && !vm.cpfOk) "CPF inválido" else null,
                    visualTransformation = Validators.cpfTransformation
                ) { vm.onCpfChange(it) }

                Spacer(Modifier.height(8.dp))
                FormSectionLabel("CONTATO")
                FormTextField(
                    "Telefone / WhatsApp", "(19) 90000-0000", vm.telefone,
                    keyboardType = KeyboardType.Phone,
                    errorText = if (vm.telefone.isNotEmpty() && !vm.telefoneOk) "Telefone inválido" else null,
                    visualTransformation = Validators.phoneTransformation
                ) { vm.onTelefoneChange(it) }
                FormTextField(
                    "E-mail", "voce@email.com", vm.email,
                    keyboardType = KeyboardType.Email,
                    errorText = if (vm.email.isNotEmpty() && !vm.emailOk) "E-mail inválido" else null
                ) { vm.onEmailChange(it) }
                FormTextField("Instagram (opcional)", "@seuperfil", vm.instagram) { vm.instagram = it }

                Spacer(Modifier.height(8.dp))
                FormSectionLabel("TRABALHO")
                FormTextField("Profissão", "Sua profissão", vm.profissao) { vm.profissao = it }
                FormTextField("Local de trabalho (opcional)", "Nome da empresa", vm.trabalhoNome) { vm.trabalhoNome = it }
                FormTextField("Endereço do trabalho (opcional)", "Rua, número, bairro", vm.trabalhoEndereco) { vm.trabalhoEndereco = it }

                Spacer(Modifier.height(8.dp))
                FormSectionLabel("FAMÍLIA")
                YesNoQuestion("Todos da família concordam com a adoção?", vm.familiaConcorda) { vm.familiaConcorda = it }
                FormTextField(
                    "Características que deseja no animal", "Porte, temperamento, idade, etc.", vm.caracteristicas,
                    singleLine = false
                ) { vm.caracteristicas = it }
                FormTextField(
                    "Motivo da adoção (opcional)", "Por que você quer adotar este animal?", vm.motivo,
                    singleLine = false
                ) { vm.motivo = it }
                YesNoQuestion("Todos os membros zelarão pela saúde e segurança do animal?", vm.membrosZelam) { vm.membrosZelam = it }
                YesNoQuestion("Há crianças na residência?", vm.temCriancas) { vm.onTemCriancasChange(it) }
                if (vm.temCriancas == true) {
                    FormTextField("Quantas crianças?", "Ex.: 2", vm.quantasCriancas, keyboardType = KeyboardType.Number) { vm.onQuantasCriancasChange(it) }
                    FormTextField("Idade das crianças", "Ex.: 5 e 9 anos", vm.idadesCriancas) { vm.idadesCriancas = it }
                }
                if (vm.temCriancas == false) {
                    YesNoQuestion("Crianças costumam visitar sua casa?", vm.criancasVisitam) { vm.onCriancasVisitamChange(it) }
                    if (vm.criancasVisitam == true) {
                        FormTextField("Com que frequência?", "Ex.: fins de semana", vm.freqVisita) { vm.freqVisita = it }
                        FormTextField("Idade dessas crianças", "Ex.: 3 e 7 anos", vm.idadeVisitantes) { vm.idadeVisitantes = it }
                    }
                }
                YesNoQuestion("Há idosos na residência?", vm.idosos) { vm.idosos = it }
                YesNoQuestion("Algum membro que terá contato com o animal tem problema respiratório?", vm.problemaRespiratorio) { vm.problemaRespiratorio = it }
            }

            2 -> {
                FormSectionLabel("ONDE O ANIMAL VAI MORAR")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormTextField(
                        "CEP", "13000-000", vm.cep,
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        errorText = when {
                            vm.cepBuscando -> null
                            vm.cep.isNotEmpty() && (!vm.cepOk || vm.cepApiInvalido) -> "CEP inválido"
                            else -> null
                        },
                        visualTransformation = Validators.cepTransformation
                    ) { vm.onCepChange(it) }
                    FormTextField("Cidade", "Campinas", vm.cidade, modifier = Modifier.weight(1f)) { vm.cidade = it }
                }
                FormTextField("Endereço completo", "Rua, número, bairro", vm.endereco) { vm.endereco = it }

                Spacer(Modifier.height(8.dp))
                FormSectionLabel("SOBRE A MORADIA")
                ChoiceQuestion("Tipo de moradia", moradiaOptions, vm.moradia) { vm.onMoradiaChange(it) }
                ChoiceQuestion("O imóvel é:", imovelOptions, vm.tipoImovel) { vm.onTipoImovelChange(it) }
                if (vm.tipoImovel == "Alugado") {
                    YesNoQuestion("No imóvel alugado é permitido ter animais?", vm.permitidoAnimais) { vm.permitidoAnimais = it }
                }
                YesNoQuestion("A casa tem muro ou portões para evitar fuga?", vm.muroPortoes) { vm.muroPortoes = it }
                if (vm.moradia == "Apartamento") {
                    YesNoQuestion("O apartamento tem telas de proteção?", vm.telasProtecao) { vm.telasProtecao = it }
                }
                FormTextField(
                    "Se algo alterar a rotina da família, o que será feito com o animal?",
                    "Descreva o que faria", vm.situacaoRotina,
                    singleLine = false
                ) { vm.situacaoRotina = it }

                Spacer(Modifier.height(8.dp))
                AttachmentPicker(
                    label = "Fotos e vídeo de onde o animal vai morar",
                    helperText = "Ajuda a equipe a avaliar o espaço onde o animal vai viver",
                    attachments = vm.anexosCasa,
                    onAttachmentsChange = { vm.anexosCasa = it }
                )
            }

            else -> {
                FormSectionLabel("OUTROS ANIMAIS")
                ChoiceQuestion("Experiência com pets", experienciaOptions, vm.experiencia) { vm.experiencia = it }
                YesNoQuestion("Tem outro animal atualmente?", vm.temOutrosAnimais) { vm.onTemOutrosAnimaisChange(it) }
                if (vm.temOutrosAnimais == true) {
                    FormTextField("Qual espécie?", "Ex.: cachorro, gato", vm.especieAnimais) { vm.especieAnimais = it }
                    FormTextField("Quantos são?", "Ex.: 2", vm.quantosAnimais, keyboardType = KeyboardType.Number) { vm.onQuantosAnimaisChange(it) }
                    FormTextField("Qual a idade deles?", "Ex.: 3 e 5 anos", vm.idadeAnimais) { vm.idadeAnimais = it }
                    YesNoQuestion("Todos são vacinados?", vm.vacinados) { vm.vacinados = it }
                    ChoiceQuestion("Qual o porte deles?", porteOptions, vm.porteAnimais) { vm.porteAnimais = it }
                }
                YesNoQuestion("Já perdeu algum animal?", vm.jaPerdeuAnimal) { vm.onJaPerdeuAnimalChange(it) }
                if (vm.jaPerdeuAnimal == true) {
                    FormTextField("Qual foi o motivo?", "Conte o que aconteceu", vm.motivoPerda, singleLine = false) { vm.motivoPerda = it }
                }

                Spacer(Modifier.height(8.dp))
                FormSectionLabel("CUIDADOS")
                FormTextField(
                    "Rotina", "Quanto tempo o animal ficará sozinho? Quem vai cuidar dele?", vm.rotina,
                    singleLine = false
                ) { vm.rotina = it }
                FormTextField("Em caso de viagem, quem cuidaria do animal?", "Descreva", vm.cuidadorViagem, singleLine = false) { vm.cuidadorViagem = it }
                FormTextField("Se o animal destruir algum bem seu, o que você fará?", "Descreva", vm.reacaoDestruicao, singleLine = false) { vm.reacaoDestruicao = it }
                FormTextField("Qual marca de ração costuma usar?", "Ex.: marca X", vm.marcaRacao) { vm.marcaRacao = it }
                YesNoQuestion("Em caso de imprevisto, terá condições financeiras de atender o animal?", vm.condicoesFinanceiras) { vm.condicoesFinanceiras = it }

                Spacer(Modifier.height(8.dp))
                FormSectionLabel("ACOMPANHAMENTO")
                YesNoQuestion("Concorda que um voluntário visite sua casa para conhecer o local e as pessoas?", vm.aceitaVisita) { vm.aceitaVisita = it }
                YesNoQuestion("Concorda em manter contato com a ONG por tempo indeterminado para informar como está o animal?", vm.mantemContato) { vm.mantemContato = it }

                Spacer(Modifier.height(8.dp))
                FormSectionLabel("AUTORIZAÇÃO")
                YesNoQuestion(
                    "Autorizo o tratamento dos meus dados pessoais para fins deste processo de adoção, conforme a LGPD.",
                    vm.lgpdConsent
                ) { vm.lgpdConsent = it }
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
        val enviando = submitState is SubmitState.Loading
        Button(
            onClick = vm::avancar,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White),
            enabled = vm.podeAvancar && !enviando
        ) {
            if (enviando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(if (vm.step < 3) "Continuar" else "Enviar solicitação")
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
