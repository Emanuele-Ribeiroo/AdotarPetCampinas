package com.adotarpet.campinas.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Mostra os dígitos digitados com máscara (CPF, telefone, CEP) sem alterar o texto guardado.
 * Como o cursor é mapeado entre o texto cru e o formatado, digitar não "embaralha" mais o número.
 */
class MaskTransformation(private val format: (String) -> String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formatted = format(text.text)
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                var digits = 0
                for ((i, c) in formatted.withIndex()) {
                    if (c.isDigit()) digits++
                    if (digits == offset) return i + 1
                }
                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int =
                formatted.take(offset).count { it.isDigit() }
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}
