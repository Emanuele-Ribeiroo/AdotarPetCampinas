package com.adotarpet.campinas.repository

import android.content.Context
import androidx.room.withTransaction
import com.adotarpet.campinas.repository.local.AppDatabase
import com.adotarpet.campinas.repository.local.entity.toEntity
import com.adotarpet.campinas.repository.local.entity.toModel
import com.adotarpet.campinas.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object UserRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var db: AppDatabase? = null
    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile

    fun init(context: Context) {
        if (db != null) return
        val database = AppDatabase.get(context)
        db = database
        val dao = database.userProfileDao()
        val prefs = context.applicationContext.getSharedPreferences("adotar_pet_user", Context.MODE_PRIVATE)
        scope.launch {

            if (prefs.all.isNotEmpty()) {
                val legacy = UserProfile(
                    name = prefs.getString("name", "").orEmpty(),
                    cpf = prefs.getString("cpf", "").orEmpty(),
                    phone = prefs.getString("phone", "").orEmpty(),
                    email = prefs.getString("email", "").orEmpty()
                )
                if (!legacy.isEmpty && dao.get() == null) dao.upsert(legacy.toEntity())
                prefs.edit().clear().apply()
            }
            dao.observe().collect { _profile.value = it?.toModel() ?: UserProfile() }
        }
    }

    fun save(profile: UserProfile) {
        val database = db ?: return
        scope.launch { database.userProfileDao().upsert(profile.toEntity()) }
    }

    fun fillBlanks(name: String = "", cpf: String = "", phone: String = "", email: String = "") {
        val database = db ?: return
        scope.launch {

            database.withTransaction {
                val dao = database.userProfileDao()
                val p = dao.get()?.toModel() ?: UserProfile()
                dao.upsert(
                    p.copy(
                        name = p.name.ifBlank { name },
                        cpf = p.cpf.ifBlank { cpf },
                        phone = p.phone.ifBlank { phone },
                        email = p.email.ifBlank { email }
                    ).toEntity()
                )
            }
        }
    }

    fun clear() {
        val database = db ?: return
        scope.launch { database.userProfileDao().clear() }
    }
}
