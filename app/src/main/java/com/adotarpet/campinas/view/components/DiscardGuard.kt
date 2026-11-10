package com.adotarpet.campinas.view.components

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.adotarpet.campinas.view.theme.Terracotta

@Composable
fun rememberDiscardGuard(hasChanges: Boolean, onDiscard: () -> Unit): () -> Unit {
    var showDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = hasChanges) { showDialog = true }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Descartar o que você preencheu?") },
            text = { Text("Se sair agora, os dados digitados serão perdidos.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onDiscard()
                }) { Text("Descartar", color = Terracotta) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Continuar editando", color = Color.Gray) }
            }
        )
    }

    return { if (hasChanges) showDialog = true else onDiscard() }
}
