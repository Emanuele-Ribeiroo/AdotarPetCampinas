package com.adotarpet.campinas

import android.app.Application
import com.adotarpet.campinas.repository.HistoryRepository
import com.adotarpet.campinas.repository.PetRepository
import com.adotarpet.campinas.repository.SettingsRepository
import com.adotarpet.campinas.repository.UserRepository

class AdotarPetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PetRepository.init(this)
        UserRepository.init(this)
        HistoryRepository.init(this)
        SettingsRepository.init(this)
    }
}
