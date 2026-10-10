package com.adotarpet.campinas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.ui.theme.GreenTag
import com.adotarpet.campinas.ui.theme.GreenTagText
import com.adotarpet.campinas.ui.theme.NeutralTagBg
import com.adotarpet.campinas.ui.theme.OutlineSoft
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TerracottaLight
import com.adotarpet.campinas.ui.theme.TextPrimary
import com.adotarpet.campinas.ui.theme.TextSecondary
import com.adotarpet.campinas.ui.theme.White

enum class TagStyle { NEUTRAL, PEACH, GREEN }

@Composable
fun InfoTag(text: String, style: TagStyle = TagStyle.NEUTRAL) {
    val (bg, fg) = when (style) {
        TagStyle.NEUTRAL -> NeutralTagBg to TextSecondary
        TagStyle.PEACH -> TerracottaLight to TextPrimary
        TagStyle.GREEN -> GreenTag to GreenTagText
    }
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = fg,
        modifier = Modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

/** Chip usado nos filtros "Todos / Cachorros / Gatos" e no formulário de resgate. */
@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) Terracotta else Color.Transparent
    val fg = if (selected) White else TextPrimary
    val shape = RoundedCornerShape(50)
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = fg,
        modifier = modifier
            .clip(shape)
            .background(color = bg, shape = shape)
            .then(
                if (!selected) Modifier.border(1.dp, OutlineSoft, shape) else Modifier
            )
            .semantics { this.selected = selected }
            .clickable(role = Role.Button) { onClick() }
            .minimumInteractiveComponentSize()
            .padding(horizontal = 18.dp, vertical = 10.dp)
    )
}
