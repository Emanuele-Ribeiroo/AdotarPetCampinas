package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.repository.SettingsRepository
import com.adotarpet.campinas.repository.ThemeMode
import com.adotarpet.campinas.repository.UserRole
import kotlinx.coroutines.flow.StateFlow

/** Estado global da MainActivity: tema, sessão e papel do usuário. */
class MainViewModel : ViewModel() {
    val themeMode: StateFlow<ThemeMode> = SettingsRepository.themeMode
    val loggedIn: StateFlow<Boolean> = SettingsRepository.loggedIn
    val role: StateFlow<UserRole> = SettingsRepository.role
}
