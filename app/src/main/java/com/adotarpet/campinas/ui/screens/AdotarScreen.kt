package com.adotarpet.campinas.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adotarpet.campinas.data.SettingsRepository
import com.adotarpet.campinas.data.UserRole
import com.adotarpet.campinas.model.PetGender
import com.adotarpet.campinas.model.PetSpecies
import com.adotarpet.campinas.ui.components.FilterChip
import com.adotarpet.campinas.ui.components.PetCard
import com.adotarpet.campinas.ui.theme.CreamCard
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TerracottaDark
import com.adotarpet.campinas.ui.theme.TextSecondary
import com.adotarpet.campinas.ui.theme.White

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun AdotarScreen(
    onConhecerClick: (String) -> Unit,
    onAdotarClick: (String) -> Unit,
    onCadastrarAnimalClick: () -> Unit,
    viewModel: AdotarViewModel = viewModel()
) {
    val filteredPets by viewModel.pets.collectAsState()
    val role by SettingsRepository.role.collectAsState()
    val isAdmin = role == UserRole.ADMIN
    val filters by viewModel.filters.collectAsState()
    val query = filters.query
    val filter = filters.species
    val gender = filters.gender
    val onlyFavorites = filters.onlyFavorites
    val hasFilters = filters.isActive

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp)
        ) {
            item {
                Text("Adotar", style = MaterialTheme.typography.headlineLarge, color = TerracottaDark)
                Text("Pet Campinas", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

                Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
                    Text(
                        "Quem está esperando por você em Campinas",
                        style = MaterialTheme.typography.titleLarge,
                        color = TerracottaDark
                    )
                }

                OutlinedTextField(
                    value = query,
                    onValueChange = viewModel::setQuery,
                    placeholder = { Text("Buscar por nome, idade, porte…", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextSecondary) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setQuery("") }) {
                                Icon(Icons.Filled.Close, contentDescription = "Limpar busca")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CreamCard,
                        focusedContainerColor = CreamCard,
                        unfocusedBorderColor = CreamCard,
                        focusedBorderColor = Terracotta
                    ),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    SpeciesFilter.entries.forEach { option ->
                        FilterChip(
                            text = option.label,
                            selected = filter == option,
                            onClick = { viewModel.setSpecies(option) }
                        )
                    }
                    FilterChip("Machos", gender == PetGender.MACHO, { viewModel.toggleGender(PetGender.MACHO) })
                    FilterChip("Fêmeas", gender == PetGender.FEMEA, { viewModel.toggleGender(PetGender.FEMEA) })
                    FilterChip("Favoritos", onlyFavorites, viewModel::toggleFavorites)
                }

                Text(
                    "ORDENAR POR",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    SortOrder.entries.forEach { option ->
                        FilterChip(option.label, filters.sort == option, { viewModel.setSort(option) })
                    }
                }
            }

            if (filteredPets.isEmpty()) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                    ) {
                        Text(
                            "Nenhum animal encontrado",
                            style = MaterialTheme.typography.titleLarge,
                            color = TerracottaDark
                        )
                        Text(
                            "Tente buscar por outro nome ou limpar os filtros.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        if (hasFilters) {
                            TextButton(
                                onClick = viewModel::clearFilters
                            ) {
                                Text("Limpar filtros", color = Terracotta)
                            }
                        }
                    }
                }
            }

            items(filteredPets, key = { it.id }) { pet ->
                PetCard(
                    pet = pet,
                    onConhecerClick = { onConhecerClick(pet.id) },
                    onAdotarClick = { onAdotarClick(pet.id) },
                    onFavoriteClick = { viewModel.toggleFavorite(pet.id) },
                    modifier = Modifier.animateItemPlacement().padding(bottom = 20.dp)
                )
            }

            // Espaço extra no fim da lista para o FAB não tampar o último card.
            item { androidx.compose.foundation.layout.Spacer(Modifier.height(64.dp)) }
        }

        if (isAdmin) {
            FloatingActionButton(
                onClick = onCadastrarAnimalClick,
                containerColor = Terracotta,
                contentColor = White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Cadastrar animal")
            }
        }
    }
}
