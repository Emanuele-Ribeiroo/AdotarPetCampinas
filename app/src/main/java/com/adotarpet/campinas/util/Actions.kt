package com.adotarpet.campinas.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.adotarpet.campinas.model.Pet

object OngInfo {
    const val PHONE_DISPLAY = "(19) 3232-0000"
    const val PHONE_DIAL = "1932320000"
    const val WHATSAPP = "5519999990000"
    const val EMAIL = "contato@adotarpetcampinas.org"
}

private fun Context.startSafely(intent: Intent, errorMessage: String) {
    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}

fun Context.dial(phone: String) =
    startSafely(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone.filter { it.isDigit() || it == '+' }}")), "Não foi possível abrir o discador")

fun Context.openWhatsApp(phone: String, message: String) =
    startSafely(
        Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$phone?text=${Uri.encode(message)}")),
        "WhatsApp não encontrado"
    )

fun Context.sendEmail(to: String, subject: String) =
    startSafely(
        Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$to?subject=${Uri.encode(subject)}")),
        "Nenhum app de e-mail encontrado"
    )

fun Context.openMap(query: String) =
    startSafely(
        Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(query)}")),
        "Nenhum app de mapas encontrado"
    )

fun Context.sharePet(pet: Pet) {
    val text = buildString {
        append("🐾 ${pet.name} está esperando um lar em ${pet.city}!\n")
        append("${pet.speciesLabel} · ${pet.genderLabel} · ${pet.age} · ${pet.size}\n")
        append("${pet.healthLabel}\n\n")
        append(pet.description)
        append("\n\nQuer adotar? Fale com o Adotar Pet Campinas: ${OngInfo.PHONE_DISPLAY}")
    }
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startSafely(Intent.createChooser(send, "Compartilhar ${pet.name}"), "Nada para compartilhar")
}

fun Context.shareText(text: String, title: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startSafely(Intent.createChooser(send, title), "Nada para compartilhar")
}
