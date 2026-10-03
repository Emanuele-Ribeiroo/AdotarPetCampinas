package com.adotarpet.campinas.data

import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.model.PetGender
import com.adotarpet.campinas.model.PetSpecies
import com.adotarpet.campinas.model.Volunteer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * Fonte de dados mock em memória. Troque por uma chamada de API / Room quando
 * o backend do app estiver definido — a UI já consome tudo via StateFlow,
 * então basta substituir a implementação interna.
 */
object PetRepository {

    private val initialPets = listOf(
        Pet(
            id = "mel",
            name = "Mel",
            species = PetSpecies.CACHORRO,
            gender = PetGender.FEMEA,
            age = "2 anos",
            size = "Porte médio",
            neutered = true,
            vaccinated = true,
            description = "Mel chegou muito magrinha e hoje é a mais brincalhona do lar temporário. " +
                "Adora criança, aceita outros cachorros e já sabe passear na guia."
        ),
        Pet(
            id = "bento",
            name = "Bento",
            species = PetSpecies.CACHORRO,
            gender = PetGender.MACHO,
            age = "4 anos",
            size = "Porte grande",
            neutered = true,
            vaccinated = true,
            description = "Bento é calmo, ótimo de apartamento e adora um cochilo no sofá. " +
                "Já é castrado e está com a vacinação em dia."
        ),
        Pet(
            id = "tobias",
            name = "Tobias",
            species = PetSpecies.GATO,
            gender = PetGender.MACHO,
            age = "8 meses",
            size = "Porte pequeno",
            neutered = true,
            vaccinated = true,
            description = "Tobias é curioso e cheio de energia, adora brincar com bolinhas de papel " +
                "e já está usando caixa de areia sem problema nenhum."
        ),
        Pet(
            id = "zeca",
            name = "Zeca",
            species = PetSpecies.GATO,
            gender = PetGender.MACHO,
            age = "3 anos",
            size = "Porte médio",
            neutered = true,
            vaccinated = true,
            description = "Zeca é tranquilo, independente e ronrona fácil. Ideal para quem quer " +
                "companhia sem muita bagunça."
        )
    )

    private val _pets = MutableStateFlow(initialPets)
    val pets: StateFlow<List<Pet>> = _pets

    val volunteers = listOf(
        Volunteer("1", "João"),
        Volunteer("2", "Rafaela"),
        Volunteer("3", "João"),
    )

    fun petById(id: String): Pet? = _pets.value.find { it.id == id }

    /** Adiciona um novo animal cadastrado (ex.: pela tela "Cadastrar animal"). */
    fun addPet(pet: Pet) {
        _pets.update { list -> list + pet }
    }

    fun toggleFavorite(id: String) {
        _pets.update { list ->
            list.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    fun favorites(): List<Pet> = _pets.value.filter { it.isFavorite }
}
