package com.adotarpet.campinas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.data.PetRepository
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.ui.components.BackButton
import com.adotarpet.campinas.ui.components.PetPhoto
import com.adotarpet.campinas.ui.theme.CreamCard
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TerracottaDark
import com.adotarpet.campinas.ui.theme.TextSecondary

/** Animais que o próprio usuário cadastrou, com atalhos para editar e excluir. */
@Composable
fun MyPetsScreen(
    onBackClick: () -> Unit,
    onPetClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onCadastrarClick: () -> Unit,
    onDeleted: (String) -> Unit
) {
    val pets by PetRepository.pets.collectAsState()
    val mine = pets.reversed()
    var toDelete by remember { mutableStateOf<Pet?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp)
    ) {
        item {
            BackButton(onBackClick)
            Text("ADMINISTRAÇÃO", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text("Gerenciar animais", style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)
            Spacer(Modifier.height(20.dp))
            if (mine.isEmpty()) {
                Text(
                    "Nenhum animal cadastrado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = onCadastrarClick, shape = RoundedCornerShape(50)) {
                    Text("Cadastrar animal", color = Terracotta)
                }
            }
        }
        items(mine, key = { it.id }) { pet ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CreamCard),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PetPhoto(
                            pet,
                            modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)),
                            iconSize = 28.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(pet.name, style = MaterialTheme.typography.titleMedium, color = TerracottaDark)
                            Text(
                                "${pet.speciesLabel} · ${pet.genderLabel} · ${pet.age}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { onPetClick(pet.id) }, shape = RoundedCornerShape(50)) {
                            Text("Ver")
                        }
                        OutlinedButton(onClick = { onEditClick(pet.id) }, shape = RoundedCornerShape(50)) {
                            Text("Editar")
                        }
                        TextButton(onClick = { toDelete = pet }) {
                            Text("Excluir", color = Terracotta)
                        }
                    }
                }
            }
        }
    }

    toDelete?.let { pet ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Excluir ${pet.name}?") },
            text = { Text("O animal sai da lista de adoção e isso não pode ser desfeito.") },
            confirmButton = {
                TextButton(onClick = {
                    PetRepository.deletePet(pet.id)
                    toDelete = null
                    onDeleted(pet.name)
                }) { Text("Excluir", color = Terracotta) }
            },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Cancelar") } }
        )
    }
}
