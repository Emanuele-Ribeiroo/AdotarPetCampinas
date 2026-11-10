package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.repository.PetRepository

class ConfirmationViewModel : ViewModel() {
    fun petById(id: String): Pet? = PetRepository.petById(id)
}
