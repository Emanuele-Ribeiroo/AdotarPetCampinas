package com.adotarpet.campinas.model

enum class PetSpecies { CACHORRO, GATO }
enum class PetGender { MACHO, FEMEA }

data class Pet(
    val id: String,
    val name: String,
    val species: PetSpecies,
    val gender: PetGender,
    val age: String,           // ex: "2 anos", "8 meses"
    val size: String,          // ex: "Porte médio"
    val neutered: Boolean,
    val vaccinated: Boolean,
    val city: String = "Campinas",
    val state: String = "SP",
    val description: String,
    val photoRes: Int? = null, // reservado para drawable local, se adicionado depois
    val isFavorite: Boolean = false
) {
    val speciesLabel: String
        get() = if (species == PetSpecies.CACHORRO) "Cachorro" else "Gato"

    val genderLabel: String
        get() = if (gender == PetGender.MACHO) "Macho" else "Fêmea"

    val healthLabel: String
        get() = when {
            neutered && vaccinated -> "Castrado · Vacinado"
            neutered -> "Castrado"
            vaccinated -> "Vacinado"
            else -> "Em avaliação"
        }
}

data class Volunteer(
    val id: String,
    val name: String
)
