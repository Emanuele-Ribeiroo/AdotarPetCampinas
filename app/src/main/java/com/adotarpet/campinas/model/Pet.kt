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
    val photoUris: List<String> = emptyList(), // fotos escolhidas na galeria ao cadastrar o animal
    val isFavorite: Boolean = false
) {
    /** Primeira foto, usada nos cards da lista. */
    val photoUri: String?
        get() = photoUris.firstOrNull()

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

data class UserProfile(
    val name: String = "",
    val cpf: String = "",
    val phone: String = "",
    val email: String = ""
) {
    val isEmpty: Boolean
        get() = name.isBlank() && cpf.isBlank() && phone.isBlank() && email.isBlank()
}

enum class HistoryType(val label: String) {
    ADOCAO("Pedido de adoção"),
    RESGATE("Aviso de resgate"),
    DOACAO("Doação"),
    VOLUNTARIO("Voluntariado"),
    CADASTRO("Animal cadastrado")
}

data class HistoryEntry(
    val id: String,
    val type: HistoryType,
    val title: String,
    val subtitle: String,
    val timestamp: Long
)
