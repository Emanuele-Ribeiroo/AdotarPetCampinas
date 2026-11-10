package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.repository.AuthRepository

class LoginViewModel : ViewModel() {
    fun login(email: String, senha: String): Boolean = AuthRepository.login(email, senha)
}
