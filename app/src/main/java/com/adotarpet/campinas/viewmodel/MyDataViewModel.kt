package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.model.UserProfile
import com.adotarpet.campinas.repository.UserRepository

class MyDataViewModel : ViewModel() {
    val perfil: UserProfile get() = UserRepository.profile.value
    fun salvar(perfil: UserProfile) = UserRepository.save(perfil)
}
