package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.repository.PetRepository
import kotlinx.coroutines.flow.StateFlow

class MyPetsViewModel : ViewModel() {
    val pets: StateFlow<List<Pet>> = PetRepository.pets
    fun deletePet(id: String) = PetRepository.deletePet(id)
}
