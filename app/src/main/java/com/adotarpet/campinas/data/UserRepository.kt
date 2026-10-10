package com.adotarpet.campinas.data

import android.content.Context
import android.content.SharedPreferences
import com.adotarpet.campinas.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Dados do usuário guardados no aparelho, usados para preencher os formulários sozinho. */
object UserRepository {
    private var prefs: SharedPreferences? = null
    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile

    fun init(context: Context) {
        if (prefs != null) return
        val p = context.applicationContext.getSharedPreferences("adotar_pet_user", Context.MODE_PRIVATE)
        prefs = p
        _profile.value = UserProfile(
            name = p.getString("name", "").orEmpty(),
            cpf = p.getString("cpf", "").orEmpty(),
            phone = p.getString("phone", "").orEmpty(),
            email = p.getString("email", "").orEmpty()
        )
    }

    fun save(profile: UserProfile) {
        _profile.value = profile
        prefs?.edit()
            ?.putString("name", profile.name)
            ?.putString("cpf", profile.cpf)
            ?.putString("phone", profile.phone)
            ?.putString("email", profile.email)
            ?.apply()
    }

    /** Preenche só os campos que o usuário ainda não tinha informado no perfil. */
    fun fillBlanks(name: String = "", cpf: String = "", phone: String = "", email: String = "") {
        val p = _profile.value
        save(
            p.copy(
                name = p.name.ifBlank { name },
                cpf = p.cpf.ifBlank { cpf },
                phone = p.phone.ifBlank { phone },
                email = p.email.ifBlank { email }
            )
        )
    }

    fun clear() = save(UserProfile())
}
