package com.adotarpet.campinas

import android.app.Application
import com.adotarpet.campinas.data.HistoryRepository
import com.adotarpet.campinas.data.PetRepository
import com.adotarpet.campinas.data.SettingsRepository
import com.adotarpet.campinas.data.UserRepository

class AdotarPetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PetRepository.init(this)
        UserRepository.init(this)
        HistoryRepository.init(this)
        SettingsRepository.init(this)
    }
}
