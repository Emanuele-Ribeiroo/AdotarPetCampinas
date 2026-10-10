package com.adotarpet.campinas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import com.adotarpet.campinas.ui.components.PetPhoto
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.data.AuthRepository
import com.adotarpet.campinas.data.HistoryRepository
import com.adotarpet.campinas.data.PetRepository
import com.adotarpet.campinas.data.SettingsRepository
import com.adotarpet.campinas.data.ThemeMode
import com.adotarpet.campinas.data.UserRole
import com.adotarpet.campinas.data.UserRepository
import com.adotarpet.campinas.ui.components.FilterChip
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.ui.theme.CreamCard
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TerracottaDark
import com.adotarpet.campinas.ui.theme.TerracottaLight
import com.adotarpet.campinas.ui.theme.TextPrimary
import com.adotarpet.campinas.ui.theme.TextSecondary

data class ProfileMenuItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit = {}
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    onPetClick: (String) -> Unit,
    onAdminPanelClick: () -> Unit,
    onMeusDadosClick: () -> Unit,
    onHistoricoClick: (String) -> Unit,
    onAjudaClick: () -> Unit
) {
    val pets by PetRepository.pets.collectAsState()
    val profile by UserRepository.profile.collectAsState()
    val themeMode by SettingsRepository.themeMode.collectAsState()
    val darkNow = themeMode == ThemeMode.DARK || (themeMode == ThemeMode.SYSTEM && isSystemInDarkTheme())
    val history by HistoryRepository.entries.collectAsState()
    val favorites = pets.filter { it.isFavorite }
    val role by SettingsRepository.role.collectAsState()
    val isAdmin = role == UserRole.ADMIN
    val accountEmail by SettingsRepository.accountEmail.collectAsState()
    val adoptions = history.count { it.type == HistoryType.ADOCAO }
    val rescues = history.count { it.type == HistoryType.RESGATE }
    var confirmClear by remember { mutableStateOf(false) }

    val menuItems = listOfNotNull(
        if (isAdmin) ProfileMenuItem(
            Icons.Filled.AdminPanelSettings,
            "Painel do administrador",
            "Cadastrar, editar, excluir e ver relatórios",
            onClick = onAdminPanelClick
        ) else null,
        ProfileMenuItem(
            Icons.Filled.Person,
            "Meus dados",
            if (profile.isEmpty) "Preencha uma vez e use em todos os formulários" else "Nome, CPF, contato",
            onClick = onMeusDadosClick
        ),
        ProfileMenuItem(
            Icons.Filled.Description,
            "Minhas solicitações",
            if (adoptions == 0) "Nenhuma ainda" else "$adoptions pedido(s) de adoção",
            onClick = { onHistoricoClick(HistoryType.ADOCAO.name) }
        ),
        ProfileMenuItem(
            Icons.Filled.WaterDrop,
            "Meus resgates",
            if (rescues == 0) "Nenhum aviso enviado" else "$rescues aviso(s) enviado(s)",
            onClick = { onHistoricoClick(HistoryType.RESGATE.name) }
        ),
        ProfileMenuItem(
            Icons.Filled.History,
            "Meu histórico",
            "Tudo que você fez no app",
            onClick = { onHistoricoClick("TODOS") }
        ),
        ProfileMenuItem(
            Icons.Filled.HelpOutline,
            "Ajuda e contato",
            "Fale com a ONG e veja parceiros",
            onClick = onAjudaClick
        ),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(TerracottaLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = TerracottaDark)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        profile.name.ifBlank { "Visitante" },
                        style = MaterialTheme.typography.titleLarge,
                        color = TerracottaDark
                    )
                    Text("${role.label} · Campinas · SP", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    if (profile.isEmpty) {
                        Text(
                            "Toque em \"Meus dados\" para completar seu perfil",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = {
                        SettingsRepository.setThemeMode(if (darkNow) ThemeMode.LIGHT else ThemeMode.DARK)
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (darkNow) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                        contentDescription = if (darkNow) "Mudar para o tema claro" else "Mudar para o tema escuro",
                        tint = TerracottaDark
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("${favorites.size}", "Favoritos", Modifier.weight(1f))
                StatCard("$adoptions", "Solicitações", Modifier.weight(1f))
                StatCard("$rescues", "Resgates", Modifier.weight(1f))
            }

            Spacer(Modifier.height(28.dp))
            Text(
                "MEUS FAVORITOS",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        item {
            if (favorites.isEmpty()) {
                Text(
                    "Você ainda não favoritou nenhum animal. Toque no coração na tela Adotar " +
                        "para guardar seus favoritos aqui.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(favorites, key = { it.id }) { pet ->
                        FavoritePetChip(pet, onClick = { onPetClick(pet.id) })
                    }
                }
            }
            Spacer(Modifier.height(28.dp))
            Text(
                "CONTA CONECTADA",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            if (accountEmail.isNotBlank()) {
                Text(accountEmail, style = MaterialTheme.typography.bodyMedium, color = TerracottaDark)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = AuthRepository::logout, shape = RoundedCornerShape(50)) {
                Text("Sair da conta", color = TerracottaDark)
            }

            Spacer(Modifier.height(28.dp))
            Text(
                "MINHA CONTA",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        items(menuItems) { item ->
            MenuRow(item)
            Spacer(Modifier.height(12.dp))
        }

        item {
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { confirmClear = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text("Limpar meus dados", color = TerracottaDark)
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Limpar seus dados?") },
            text = { Text("Apaga seu perfil e seu histórico neste celular. Os animais cadastrados e os favoritos continuam.") },
            confirmButton = {
                TextButton(onClick = {
                    UserRepository.clear()
                    HistoryRepository.clear()
                    confirmClear = false
                }) { Text("Limpar", color = Terracotta) }
            },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CreamCard)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium, color = TerracottaDark)
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
private fun FavoritePetChip(pet: Pet, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            PetPhoto(pet, modifier = Modifier.fillMaxSize(), iconSize = 36.dp)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(28.dp)
                    .background(androidx.compose.ui.graphics.Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Terracotta,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(pet.name, style = MaterialTheme.typography.titleMedium, color = TerracottaDark)
        Text("${pet.speciesLabel} · ${pet.age}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Ver perfil", style = MaterialTheme.typography.bodySmall, color = Terracotta)
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Terracotta,
                modifier = Modifier.size(14.dp).padding(start = 2.dp)
            )
        }
    }
}

@Composable
private fun MenuRow(item: ProfileMenuItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CreamCard),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(TerracottaLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, contentDescription = null, tint = TerracottaDark)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextSecondary)
        }
    }
}
