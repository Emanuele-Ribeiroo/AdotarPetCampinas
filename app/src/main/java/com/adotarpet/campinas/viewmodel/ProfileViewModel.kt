package com.adotarpet.campinas.viewmodel

import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.model.HistoryEntry
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.model.UserProfile
import com.adotarpet.campinas.repository.AuthRepository
import com.adotarpet.campinas.repository.HistoryRepository
import com.adotarpet.campinas.repository.PetRepository
import com.adotarpet.campinas.repository.SettingsRepository
import com.adotarpet.campinas.repository.ThemeMode
import com.adotarpet.campinas.repository.UserRepository
import com.adotarpet.campinas.repository.UserRole
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {
    val pets: StateFlow<List<Pet>> = PetRepository.pets
    val profile: StateFlow<UserProfile> = UserRepository.profile
    val themeMode: StateFlow<ThemeMode> = SettingsRepository.themeMode
    val history: StateFlow<List<HistoryEntry>> = HistoryRepository.entries
    val role: StateFlow<UserRole> = SettingsRepository.role
    val accountEmail: StateFlow<String> = SettingsRepository.accountEmail

    fun setThemeMode(mode: ThemeMode) = SettingsRepository.setThemeMode(mode)
    fun logout() = AuthRepository.logout()

    fun limparDados() {
        UserRepository.clear()
        HistoryRepository.clear()
    }
}
