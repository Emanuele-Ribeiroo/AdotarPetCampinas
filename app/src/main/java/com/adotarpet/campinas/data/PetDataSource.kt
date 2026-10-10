package com.adotarpet.campinas.data

import com.adotarpet.campinas.model.Pet
import kotlinx.coroutines.flow.StateFlow

/**
 * Contrato dos dados de animais. Hoje é implementado por [PetRepository] (mock + armazenamento
 * local); quando existir uma API, basta criar outra implementação e trocar onde ela é usada.
 */
interface PetDataSource {
    val pets: StateFlow<List<Pet>>
    fun petById(id: String): Pet?
    fun addPet(pet: Pet)
    fun updatePet(pet: Pet)
    fun deletePet(id: String)
    fun toggleFavorite(id: String)
}
