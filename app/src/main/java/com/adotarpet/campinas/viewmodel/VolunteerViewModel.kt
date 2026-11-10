package com.adotarpet.campinas.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.model.UserProfile
import com.adotarpet.campinas.repository.HistoryRepository
import com.adotarpet.campinas.repository.UserRepository
import com.adotarpet.campinas.util.Validators

/** Estado e regras do cadastro de voluntário. */
class VolunteerViewModel : ViewModel() {

    val areas = listOf("Lar temporário", "Passeios", "Eventos de adoção", "Transporte", "Divulgação", "Veterinária")
    val disponibilidades = listOf("Dias úteis", "Fins de semana", "Qualquer dia")

    private val perfil: UserProfile = UserRepository.profile.value

    var nome by mutableStateOf(perfil.name)
    var telefone by mutableStateOf(perfil.phone)
        private set
    var email by mutableStateOf(perfil.email)
        private set
    var areasSelecionadas by mutableStateOf(setOf<String>())
        private set
    var disponibilidade by mutableStateOf(disponibilidades.first())
    var mensagem by mutableStateOf("")
    var enviado by mutableStateOf(false)
        private set

    val telefoneOk: Boolean get() = Validators.isValidPhone(telefone)
    val emailOk: Boolean get() = Validators.isValidEmail(email)
    val podeEnviar: Boolean get() = nome.isNotBlank() && telefoneOk && emailOk && areasSelecionadas.isNotEmpty()

    val temAlteracoes: Boolean
        get() = !enviado && (nome != perfil.name || telefone != perfil.phone || email != perfil.email ||
            areasSelecionadas.isNotEmpty() || mensagem.isNotBlank())

    fun onTelefoneChange(texto: String) { telefone = Validators.onlyPhone(texto) }
    fun onEmailChange(texto: String) { email = texto.trim() }

    fun alternarArea(area: String) {
        areasSelecionadas = if (area in areasSelecionadas) areasSelecionadas - area else areasSelecionadas + area
    }

    fun enviarCadastro() {
        if (!podeEnviar) return
        UserRepository.fillBlanks(name = nome.trim(), phone = telefone, email = email)
        HistoryRepository.add(
            HistoryType.VOLUNTARIO,
            "Cadastro de voluntário",
            areasSelecionadas.sorted().joinToString(", ")
        )
        enviado = true
    }
}
