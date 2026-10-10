package com.adotarpet.campinas.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ThemeMode(val label: String) {
    LIGHT("Claro"),
    DARK("Escuro"),
    SYSTEM("Padrão do dispositivo")
}

/** Tipo de perfil: o administrador gerencia animais e vê relatórios; o usuário só usa o app. */
enum class UserRole(val label: String) {
    USER("Usuário"),
    ADMIN("Administrador")
}

/** Preferências e sessão guardadas no aparelho: tema, tipo de perfil e se há alguém conectado. */
object SettingsRepository {
    private var prefs: SharedPreferences? = null

    private val _themeMode = MutableStateFlow(ThemeMode.LIGHT)
    val themeMode: StateFlow<ThemeMode> = _themeMode

    private val _role = MutableStateFlow(UserRole.USER)
    val role: StateFlow<UserRole> = _role

    private val _loggedIn = MutableStateFlow(false)
    val loggedIn: StateFlow<Boolean> = _loggedIn

    private val _accountEmail = MutableStateFlow("")
    val accountEmail: StateFlow<String> = _accountEmail

    fun init(context: Context) {
        if (prefs != null) return
        val p = context.applicationContext.getSharedPreferences("adotar_pet_settings", Context.MODE_PRIVATE)
        prefs = p
        _themeMode.value = runCatching { ThemeMode.valueOf(p.getString("theme", null).orEmpty()) }
            .getOrDefault(ThemeMode.LIGHT)
        _role.value = runCatching { UserRole.valueOf(p.getString("role", null).orEmpty()) }
            .getOrDefault(UserRole.USER)
        _loggedIn.value = p.getBoolean("logged_in", false)
        _accountEmail.value = p.getString("account_email", "").orEmpty()
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs?.edit()?.putString("theme", mode.name)?.apply()
    }

    /** Registra a sessão depois de um login válido (veja [AuthRepository]). */
    fun signIn(role: UserRole, email: String) {
        _role.value = role
        _accountEmail.value = email
        _loggedIn.value = true
        prefs?.edit()
            ?.putString("role", role.name)
            ?.putString("account_email", email)
            ?.putBoolean("logged_in", true)
            ?.apply()
    }

    fun signOut() {
        _role.value = UserRole.USER
        _accountEmail.value = ""
        _loggedIn.value = false
        prefs?.edit()
            ?.putString("role", UserRole.USER.name)
            ?.remove("account_email")
            ?.putBoolean("logged_in", false)
            ?.apply()
    }
}
