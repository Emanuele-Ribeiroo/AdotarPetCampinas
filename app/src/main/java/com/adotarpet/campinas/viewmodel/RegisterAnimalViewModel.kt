package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.repository.HistoryRepository
import com.adotarpet.campinas.repository.PetRepository

class RegisterAnimalViewModel : ViewModel() {
    fun petById(id: String): Pet? = PetRepository.petById(id)

    fun atualizar(pet: Pet) = PetRepository.updatePet(pet)

    fun salvarNovo(pet: Pet) {
        PetRepository.addPet(pet)
        HistoryRepository.add(HistoryType.CADASTRO, "Cadastro de ${pet.name}", "${pet.speciesLabel} · ${pet.age}")
    }
}
