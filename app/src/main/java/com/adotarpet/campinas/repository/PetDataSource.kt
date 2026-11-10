package com.adotarpet.campinas.repository

import com.adotarpet.campinas.model.Pet
import kotlinx.coroutines.flow.StateFlow

interface PetDataSource {
    val pets: StateFlow<List<Pet>>
    fun petById(id: String): Pet?
    fun addPet(pet: Pet)
    fun updatePet(pet: Pet)
    fun deletePet(id: String)
    fun toggleFavorite(id: String)
}
