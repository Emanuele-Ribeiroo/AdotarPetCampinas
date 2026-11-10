package com.adotarpet.campinas.util

import com.adotarpet.campinas.model.HistoryEntry
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.model.PetGender
import com.adotarpet.campinas.model.PetSpecies

data class Report(
    val totalPets: Int,
    val dogs: Int,
    val cats: Int,
    val males: Int,
    val females: Int,
    val neutered: Int,
    val vaccinated: Int,
    val favorites: Int,
    val countByType: Map<HistoryType, Int>
) {
    fun percent(part: Int): Int = if (totalPets == 0) 0 else part * 100 / totalPets
}

fun buildReport(pets: List<Pet>, history: List<HistoryEntry>): Report = Report(
    totalPets = pets.size,
    dogs = pets.count { it.species == PetSpecies.CACHORRO },
    cats = pets.count { it.species == PetSpecies.GATO },
    males = pets.count { it.gender == PetGender.MACHO },
    females = pets.count { it.gender == PetGender.FEMEA },
    neutered = pets.count { it.neutered },
    vaccinated = pets.count { it.vaccinated },
    favorites = pets.count { it.isFavorite },
    countByType = HistoryType.entries.associateWith { type -> history.count { it.type == type } }
)

fun Report.toText(history: List<HistoryEntry>): String = buildString {
    appendLine("Relatório — Adotar Pet Campinas")
    appendLine()
    appendLine("ANIMAIS")
    appendLine("Total: $totalPets (cães: $dogs · gatos: $cats)")
    appendLine("Machos: $males · Fêmeas: $females")
    appendLine("Castrados: $neutered (${percent(neutered)}%) · Vacinados: $vaccinated (${percent(vaccinated)}%)")
    appendLine("Favoritados: $favorites")
    appendLine()
    appendLine("ATIVIDADE")
    HistoryType.entries.forEach { type -> appendLine("${type.label}: ${countByType[type] ?: 0}") }
    if (history.isNotEmpty()) {
        appendLine()
        appendLine("ÚLTIMOS REGISTROS")
        history.take(10).forEach { appendLine("• ${it.type.label}: ${it.title}" + if (it.subtitle.isBlank()) "" else " (${it.subtitle})") }
    }
}
