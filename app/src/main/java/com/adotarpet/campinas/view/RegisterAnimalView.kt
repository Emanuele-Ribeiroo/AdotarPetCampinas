package com.adotarpet.campinas.view

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adotarpet.campinas.viewmodel.RegisterAnimalViewModel
import com.adotarpet.campinas.model.PetGender
import com.adotarpet.campinas.model.PetSpecies
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

@Composable
fun RegisterAnimalView(
    petId: String? = null,
    onSaved: (editado: Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    // Estado e regras ficam no ViewModel; a tela só desenha e dispara eventos.
    val vm: RegisterAnimalViewModel = viewModel { RegisterAnimalViewModel(petId) }
    val editando = vm.editando

    val tentarVoltar = rememberDiscardGuard(hasChanges = vm.temAlteracoes, onDiscard = onBackClick)

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
        FormTextField("Nome", "Como ele se chama?", vm.nome) { vm.nome = it }

        Text("Espécie", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 16.dp)) {
            FilterChip("Cachorro", vm.especie == PetSpecies.CACHORRO, { vm.especie = PetSpecies.CACHORRO })
            FilterChip("Gato", vm.especie == PetSpecies.GATO, { vm.especie = PetSpecies.GATO })
        }

        Text("Sexo", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 16.dp)) {
            FilterChip("Macho", vm.genero == PetGender.MACHO, { vm.genero = PetGender.MACHO })
            FilterChip("Fêmea", vm.genero == PetGender.FEMEA, { vm.genero = PetGender.FEMEA })
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormTextField("Idade", "2 anos", vm.idade, modifier = Modifier.weight(1f)) { vm.idade = it }
            FormTextField("Porte", "Médio", vm.porte, modifier = Modifier.weight(1f)) { vm.porte = it }
        }

        Text("Saúde", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 16.dp)) {
            FilterChip("Castrado", vm.castrado, { vm.castrado = !vm.castrado })
            FilterChip("Vacinado", vm.vacinado, { vm.vacinado = !vm.vacinado })
        }

        FormTextField("Descrição", "Conte um pouco sobre ele", vm.descricao, singleLine = false) { vm.descricao = it }

        AttachmentPicker(
            label = "Fotos e vídeo do animal",
            helperText = "A primeira foto aparece na lista; as outras ficam na galeria do animal",
            attachments = vm.anexos,
            onAttachmentsChange = { vm.anexos = it }
        )

        Spacer(Modifier.height(28.dp))
        Button(
            onClick = {
                // Só o Android sabe dizer se o arquivo escolhido é imagem ou vídeo.
                vm.salvar(ehImagem = { uri -> context.contentResolver.getType(uri)?.startsWith("image/") == true })
                onSaved(editando)
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White),
            enabled = vm.podeSalvar
        ) {
            Text(if (editando) "Salvar alterações" else "Cadastrar animal")
        }
        Spacer(Modifier.height(24.dp))
    }
}
