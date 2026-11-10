package com.adotarpet.campinas

import com.adotarpet.campinas.repository.AuthRepository
import com.adotarpet.campinas.repository.SettingsRepository
import com.adotarpet.campinas.repository.UserRole
import com.adotarpet.campinas.model.HistoryEntry
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.model.PetGender
import com.adotarpet.campinas.model.PetSpecies
import com.adotarpet.campinas.util.buildReport
import com.adotarpet.campinas.util.toText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportsTest {

    private fun pet(id: String, species: PetSpecies, gender: PetGender, neutered: Boolean, vaccinated: Boolean, fav: Boolean = false) =
        Pet(
            id = id, name = id, species = species, gender = gender, age = "1 ano", size = "Pequeno",
            neutered = neutered, vaccinated = vaccinated, description = "", isFavorite = fav
        )

    @Test
    fun conta_animais_e_atividade() {
        val pets = listOf(
            pet("a", PetSpecies.CACHORRO, PetGender.MACHO, neutered = true, vaccinated = true, fav = true),
            pet("b", PetSpecies.GATO, PetGender.FEMEA, neutered = false, vaccinated = true),
            pet("c", PetSpecies.GATO, PetGender.FEMEA, neutered = false, vaccinated = false)
        )
        val history = listOf(
            HistoryEntry("1", HistoryType.ADOCAO, "Adoção de a", "", 1),
            HistoryEntry("2", HistoryType.ADOCAO, "Adoção de b", "", 2),
            HistoryEntry("3", HistoryType.DOACAO, "Doação", "R$ 10", 3)
        )
        val report = buildReport(pets, history)

        assertEquals(3, report.totalPets)
        assertEquals(1, report.dogs)
        assertEquals(2, report.cats)
        assertEquals(1, report.males)
        assertEquals(2, report.females)
        assertEquals(1, report.favorites)
        assertEquals(33, report.percent(report.neutered))
        assertEquals(66, report.percent(report.vaccinated))
        assertEquals(2, report.countByType[HistoryType.ADOCAO])
        assertEquals(0, report.countByType[HistoryType.RESGATE])
        assertTrue(report.toText(history).contains("Total: 3"))
    }

    @Test
    fun relatorio_vazio_nao_quebra() {
        val report = buildReport(emptyList(), emptyList())
        assertEquals(0, report.percent(report.neutered))
    }

    @Test
    fun login_mockado() {
        assertEquals(UserRole.USER, AuthRepository.authenticate("usuario@adotarpet.com", "usuario123"))
        assertEquals(UserRole.ADMIN, AuthRepository.authenticate(" ADMIN@adotarpet.com ", "admin123"))
        assertNull(AuthRepository.authenticate("admin@adotarpet.com", "usuario123"))
        assertNull(AuthRepository.authenticate("outro@email.com", "admin123"))
    }

    @Test
    fun sessao_entra_e_sai() {
        assertFalse(AuthRepository.login("admin@adotarpet.com", "errada"))
        assertFalse(SettingsRepository.loggedIn.value)

        assertTrue(AuthRepository.login("admin@adotarpet.com", "admin123"))
        assertTrue(SettingsRepository.loggedIn.value)
        assertEquals(UserRole.ADMIN, SettingsRepository.role.value)

        AuthRepository.logout()
        assertFalse(SettingsRepository.loggedIn.value)
        assertEquals(UserRole.USER, SettingsRepository.role.value)
    }
}
