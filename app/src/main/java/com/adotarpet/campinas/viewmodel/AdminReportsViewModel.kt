package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.model.HistoryEntry
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.repository.HistoryRepository
import com.adotarpet.campinas.repository.PetRepository
import kotlinx.coroutines.flow.StateFlow

class AdminReportsViewModel : ViewModel() {
    val pets: StateFlow<List<Pet>> = PetRepository.pets
    val history: StateFlow<List<HistoryEntry>> = HistoryRepository.entries
}
