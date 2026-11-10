package com.adotarpet.campinas.model

enum class PetSpecies { CACHORRO, GATO }
enum class PetGender { MACHO, FEMEA }

data class Pet(
    val id: String,
    val name: String,
    val species: PetSpecies,
    val gender: PetGender,
    val age: String,
    val size: String,
    val neutered: Boolean,
    val vaccinated: Boolean,
    val city: String = "Campinas",
    val state: String = "SP",
    val description: String,
    val photoRes: Int? = null,
    val photoUris: List<String> = emptyList(),
    val isFavorite: Boolean = false
) {

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
    val name: String,
    val photoRes: Int? = null
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

enum class RequestStatus(val label: String) {
    EM_ANALISE("Em análise"),
    CANCELADA("Cancelada")
}

data class HistoryEntry(
    val id: String,
    val type: HistoryType,
    val title: String,
    val subtitle: String,
    val timestamp: Long,
    val status: RequestStatus = RequestStatus.EM_ANALISE,
    val details: String = "",
    val motivoCancelamento: String = ""
) {
    val cancelavel: Boolean
        get() = status == RequestStatus.EM_ANALISE &&
            (type == HistoryType.ADOCAO || type == HistoryType.RESGATE)
}
