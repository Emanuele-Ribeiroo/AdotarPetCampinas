package com.adotarpet.campinas.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.adotarpet.campinas.ui.theme.CreamCard
import com.adotarpet.campinas.ui.theme.SurfaceWhite
import com.adotarpet.campinas.ui.theme.Terracotta
import com.adotarpet.campinas.ui.theme.TerracottaDark
import com.adotarpet.campinas.ui.theme.TerracottaLight
import com.adotarpet.campinas.ui.theme.TextSecondary
import com.adotarpet.campinas.ui.theme.White

/** Arquivo (foto ou vídeo) anexado pelo usuário em um formulário. */
data class Attachment(val uri: Uri, val name: String)

@Composable
fun FormSectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
fun FormTextField(
    label: String,
    placeholder: String,
    value: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorText: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = CreamCard,
                focusedContainerColor = CreamCard,
                unfocusedBorderColor = CreamCard,
                focusedBorderColor = Terracotta
            ),
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = errorText != null,
            supportingText = errorText?.let { { Text(it) } }
        )
    }
}

/** Seletor de fotos/vídeos da galeria; mostra os arquivos escolhidos e permite removê-los. */
@Composable
fun AttachmentPicker(
    label: String,
    helperText: String,
    attachments: List<Attachment>,
    onAttachmentsChange: (List<Attachment>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        val novos = uris.mapIndexed { i, uri ->
            // Mantém o acesso ao arquivo mesmo depois de o seletor fechar.
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) {
            }
            Attachment(uri, uri.lastPathSegment?.substringAfterLast('/') ?: "arquivo ${attachments.size + i + 1}")
        }
        onAttachmentsChange((attachments + novos).distinctBy { it.uri })
    }

    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CreamCard),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { launcher.launch(arrayOf("image/*", "video/*")) }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).background(SurfaceWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.AddAPhoto, contentDescription = null, tint = TerracottaDark)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        if (attachments.isEmpty()) "Toque para anexar fotos ou vídeo"
                        else "${attachments.size} arquivo(s) anexado(s) ✓",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(helperText, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 8.dp)) {
            attachments.forEach { anexo ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        anexo.name,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "Remover",
                        style = MaterialTheme.typography.bodySmall,
                        color = Terracotta,
                        modifier = Modifier
                            .clickable { onAttachmentsChange(attachments - anexo) }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
