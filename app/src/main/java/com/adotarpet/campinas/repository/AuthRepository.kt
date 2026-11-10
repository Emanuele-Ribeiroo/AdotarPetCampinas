package com.adotarpet.campinas.repository

object AuthRepository {
    data class MockAccount(val email: String, val password: String, val role: UserRole)

    val accounts = listOf(
        MockAccount("usuario@adotarpet.com", "usuario123", UserRole.USER),
        MockAccount("admin@adotarpet.com", "admin123", UserRole.ADMIN)
    )

    fun authenticate(email: String, password: String): UserRole? =
        accounts.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) && it.password == password }?.role

    fun login(email: String, password: String): Boolean {
        val role = authenticate(email, password) ?: return false
        SettingsRepository.signIn(role, email.trim().lowercase())
        return true
    }

    /** Entra sem conta: usuário comum, sem e-mail associado. */
    fun loginAsGuest() = SettingsRepository.signIn(UserRole.USER, "")

    fun logout()= SettingsRepository.signOut()
}
