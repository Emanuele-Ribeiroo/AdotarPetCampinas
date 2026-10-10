package com.adotarpet.campinas.data

/**
 * Login MOCKADO. As contas e senhas abaixo ficam dentro do app, então qualquer pessoa que
 * tenha o app consegue descobri-las: servem só para demonstração. Quando existir um servidor,
 * troque [authenticate] por uma chamada de API (e-mail + senha verificados no backend).
 */
object AuthRepository {
    data class MockAccount(val email: String, val password: String, val role: UserRole)

    val accounts = listOf(
        MockAccount("usuario@adotarpet.com", "usuario123", UserRole.USER),
        MockAccount("admin@adotarpet.com", "admin123", UserRole.ADMIN)
    )

    /** Retorna o tipo de perfil se e-mail e senha conferem, ou `null` se estiverem errados. */
    fun authenticate(email: String, password: String): UserRole? =
        accounts.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) && it.password == password }?.role

    /** Valida o login e, se estiver certo, inicia a sessão. Retorna se deu certo. */
    fun login(email: String, password: String): Boolean {
        val role = authenticate(email, password) ?: return false
        SettingsRepository.signIn(role, email.trim().lowercase())
        return true
    }

    fun logout() = SettingsRepository.signOut()
}
