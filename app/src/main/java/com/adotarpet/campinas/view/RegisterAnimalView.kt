package com.adotarpet.campinas.view

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adotarpet.campinas.viewmodel.RegisterAnimalViewModel
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.model.PetGender
import com.adotarpet.campinas.model.PetSpecies
import com.adotarpet.campinas.view.components.Attachment
import com.adotarpet.campinas.view.components.AttachmentPicker
import com.adotarpet.campinas.view.components.BackButton
import com.adotarpet.campinas.view.components.FilterChip
import com.adotarpet.campinas.view.components.FormSectionLabel
import com.adotarpet.campinas.view.components.FormTextField
import com.adotarpet.campinas.view.components.rememberDiscardGuard
import com.adotarpet.campinas.view.theme.Terracotta
import com.adotarpet.campinas.view.theme.TerracottaDark
import com.adotarpet.campinas.view.theme.TextSecondary
import com.adotarpet.campinas.view.theme.White
import java.util.UUID

@Composable
fun RegisterAnimalView(
    petId: String? = null,
    onSaved: (editado: Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val vm: RegisterAnimalViewModel = viewModel()
    val existing = remember(petId) { petId?.let { vm.petById(it) } }
    val editando = existing != null

    var nome by remember { mutableStateOf(existing?.name.orEmpty()) }
    var especie by remember { mutableStateOf(existing?.species ?: PetSpecies.CACHORRO) }
    var genero by remember { mutableStateOf(existing?.gender ?: PetGender.MACHO) }
    var idade by remember { mutableStateOf(existing?.age.orEmpty()) }
    var porte by remember { mutableStateOf(existing?.size.orEmpty()) }
    var castrado by remember { mutableStateOf(existing?.neutered ?: false) }
    var vacinado by remember { mutableStateOf(existing?.vaccinated ?: false) }
    var descricao by remember { mutableStateOf(existing?.description.orEmpty()) }
    var anexos by remember {
        mutableStateOf(existing?.photoUris.orEmpty().mapIndexed { i, uri -> Attachment(Uri.parse(uri), "Foto ${i + 1}") })
    }

    val tentarVoltar = rememberDiscardGuard(
        hasChanges = !editando && (
            nome.isNotBlank() || idade.isNotBlank() || porte.isNotBlank() ||
                descricao.isNotBlank() || castrado || vacinado || anexos.isNotEmpty()
            ),
        onDiscard = onBackClick
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        BackButton(tentarVoltar)
        Text(
            if (editando) "EDIÇÃO" else "CADASTRO",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            if (editando) "Editar animal" else "Cadastrar animal",
            style = MaterialTheme.typography.headlineMedium,
            color = TerracottaDark
        )

        Spacer(Modifier.height(24.dp))
        FormSectionLabel("SOBRE O ANIMAL")
        FormTextField("Nome", "Como ele se chama?", nome) { nome = it }

        Text("Espécie", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 16.dp)) {
            FilterChip("Cachorro", especie == PetSpecies.CACHORRO, { especie = PetSpecies.CACHORRO })
            FilterChip("Gato", especie == PetSpecies.GATO, { especie = PetSpecies.GATO })
        }

        Text("Sexo", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 16.dp)) {
            FilterChip("Macho", genero == PetGender.MACHO, { genero = PetGender.MACHO })
            FilterChip("Fêmea", genero == PetGender.FEMEA, { genero = PetGender.FEMEA })
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormTextField("Idade", "2 anos", idade, modifier = Modifier.weight(1f)) { idade = it }
            FormTextField("Porte", "Médio", porte, modifier = Modifier.weight(1f)) { porte = it }
        }

        Text("Saúde", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 16.dp)) {
            FilterChip("Castrado", castrado, { castrado = !castrado })
            FilterChip("Vacinado", vacinado, { vacinado = !vacinado })
        }

        FormTextField("Descrição", "Conte um pouco sobre ele", descricao, singleLine = false) { descricao = it }

        AttachmentPicker(
            label = "Fotos e vídeo do animal",
            helperText = "A primeira foto aparece na lista; as outras ficam na galeria do animal",
            attachments = anexos,
            onAttachmentsChange = { anexos = it }
        )

        Spacer(Modifier.height(28.dp))
        Button(
            onClick = {
                val fotosAntigas = existing?.photoUris.orEmpty()

                val fotos = anexos.filter { anexo ->
                    anexo.uri.toString() in fotosAntigas ||
                        context.contentResolver.getType(anexo.uri)?.startsWith("image/") == true
                }.map { it.uri.toString() }

                if (existing != null) {
                    vm.atualizar(
                        existing.copy(
                            name = nome.trim(),
                            species = especie,
                            gender = genero,
                            age = idade.trim().ifBlank { "Idade não informada" },
                            size = porte.trim().ifBlank { "Porte não informado" },
                            neutered = castrado,
                            vaccinated = vacinado,
                            description = descricao.trim(),
                            photoUris = fotos
                        )
                    )
                } else {
                    val novo = Pet(
                        id = UUID.randomUUID().toString(),
                        name = nome.trim(),
                        species = especie,
                        gender = genero,
                        age = idade.trim().ifBlank { "Idade não informada" },
                        size = porte.trim().ifBlank { "Porte não informado" },
                        neutered = castrado,
                        vaccinated = vacinado,
                        description = descricao.trim(),
                        photoUris = fotos
                    )
                    vm.salvarNovo(novo)
                }
                onSaved(editando)
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White),
            enabled = nome.isNotBlank()
        ) {
            Text(if (editando) "Salvar alterações" else "Cadastrar animal")
        }
        Spacer(Modifier.height(24.dp))
    }
}
