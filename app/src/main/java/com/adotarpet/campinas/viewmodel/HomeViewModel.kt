package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.model.Volunteer
import com.adotarpet.campinas.repository.PetRepository

class HomeViewModel : ViewModel() {
    val volunteers: List<Volunteer> = PetRepository.volunteers
}
