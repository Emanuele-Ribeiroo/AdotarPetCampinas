package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.repository.PetRepository
import com.adotarpet.campinas.repository.SettingsRepository
import com.adotarpet.campinas.repository.UserRole
import kotlinx.coroutines.flow.StateFlow

class PetDetailViewModel : ViewModel() {
    val pets: StateFlow<List<Pet>> = PetRepository.pets
    val role: StateFlow<UserRole> = SettingsRepository.role

    fun toggleFavorite(id: String) = PetRepository.toggleFavorite(id)
    fun deletePet(id: String) = PetRepository.deletePet(id)
}
