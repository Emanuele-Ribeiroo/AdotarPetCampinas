package com.adotarpet.campinas.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adotarpet.campinas.repository.UserRole
import com.adotarpet.campinas.viewmodel.PetDetailViewModel
import com.adotarpet.campinas.view.components.InfoTag
import com.adotarpet.campinas.view.components.PetPhoto
import com.adotarpet.campinas.view.components.TagStyle
import com.adotarpet.campinas.view.theme.BrandBrown
import com.adotarpet.campinas.view.theme.CreamBackground
import com.adotarpet.campinas.view.theme.CreamCard
import com.adotarpet.campinas.view.theme.Terracotta
import com.adotarpet.campinas.view.theme.TerracottaDark
import com.adotarpet.campinas.view.theme.TextPrimary
import com.adotarpet.campinas.view.theme.TextSecondary
import com.adotarpet.campinas.view.theme.White
import com.adotarpet.campinas.util.OngInfo
import com.adotarpet.campinas.util.openWhatsApp
import com.adotarpet.campinas.util.sharePet

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PetDetailView(
    petId: String,
    onBackClick: () -> Unit,
    onAdotarClick: (String) -> Unit,
    onEditClick: (String) -> Unit = {},
    onDeleted: (String) -> Unit = {}
) {
    val vm: PetDetailViewModel = viewModel()
    val pets by vm.pets.collectAsState()
    val pet = pets.find { it.id == petId } ?: return
    val context = LocalContext.current
    val role by vm.role.collectAsState()
    val isAdmin = role == UserRole.ADMIN
    var confirmDelete by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.05f)
            ) {
                val photoCount = pet.photoUris.size
                if (photoCount > 1) {
                    val pagerState = rememberPagerState(pageCount = { photoCount })
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                        PetPhoto(pet.copy(photoUris = listOf(pet.photoUris[page])), modifier = Modifier.fillMaxSize())
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(photoCount) { index ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == pagerState.currentPage) 10.dp else 7.dp)
                                    .background(
                                        if (index == pagerState.currentPage) White else White.copy(alpha = 0.55f),
                                        CircleShape
                                    )
                            )
                        }
                    }
                } else {
                    PetPhoto(pet, modifier = Modifier.fillMaxSize(), iconSize = 140.dp)
                }
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .size(44.dp)
                        .background(White, CircleShape)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = BrandBrown)
                }
                IconButton(
                    onClick = { context.sharePet(pet) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(44.dp)
                        .background(White, CircleShape)
                ) {
                    Icon(Icons.Filled.Share, contentDescription = "Compartilhar ${pet.name}", tint = BrandBrown)
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(pet.name, style = MaterialTheme.typography.headlineLarge, color = TerracottaDark)
                    Text(
                        "${pet.city} · ${pet.state}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoTag(pet.speciesLabel, TagStyle.PEACH)
                    InfoTag(pet.genderLabel, TagStyle.PEACH)
                    InfoTag(pet.age, TagStyle.NEUTRAL)
                }
                Spacer(Modifier.height(8.dp))
                Row {
                    InfoTag(pet.size, TagStyle.NEUTRAL)
                }
                Spacer(Modifier.height(8.dp))
                InfoTag(pet.healthLabel, TagStyle.GREEN)

                Spacer(Modifier.height(20.dp))
                Text(pet.description, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)

                if (isAdmin) {
                    Spacer(Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onEditClick(pet.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(50)
                        ) { Text("Editar", color = TerracottaDark) }
                        OutlinedButton(
                            onClick = { confirmDelete = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(50)
                        ) { Text("Excluir", color = Terracotta) }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CreamBackground)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedIconButton(
                onClick = { vm.toggleFavorite(pet.id) },
                modifier = Modifier.size(52.dp),
                border = BorderStroke(1.dp, Terracotta)
            ) {
                Icon(
                    imageVector = if (pet.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (pet.isFavorite) "Remover ${pet.name} dos favoritos" else "Favoritar ${pet.name}",
                    tint = Terracotta
                )
            }
            Button(
                onClick = { onAdotarClick(pet.id) },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = White)
            ) {
                Text("Quero adotar ${pet.name}")
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Excluir ${pet.name}?") },
            text = { Text("O animal sai da lista de adoção e isso não pode ser desfeito.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    vm.deletePet(pet.id)
                    onDeleted(pet.name)
                }) { Text("Excluir", color = Terracotta) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") } }
        )
    }
}
