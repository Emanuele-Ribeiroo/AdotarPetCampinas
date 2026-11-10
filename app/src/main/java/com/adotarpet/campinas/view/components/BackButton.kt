package com.adotarpet.campinas.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.view.theme.CreamCard
import com.adotarpet.campinas.view.theme.TerracottaDark

@Composable
fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .padding(bottom = 12.dp)
            .size(48.dp)
            .background(CreamCard, CircleShape)
    ) {
        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = TerracottaDark)
    }
}
