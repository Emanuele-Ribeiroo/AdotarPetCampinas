package com.adotarpet.campinas

import com.adotarpet.campinas.data.HistoryRepository
import com.adotarpet.campinas.data.PetRepository
import com.adotarpet.campinas.model.HistoryEntry
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.model.PetGender
import com.adotarpet.campinas.model.PetSpecies
import com.adotarpet.campinas.ui.screens.AdotarFilters
import com.adotarpet.campinas.ui.screens.SortOrder
import com.adotarpet.campinas.ui.screens.SpeciesFilter
import com.adotarpet.campinas.ui.screens.applyFilters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FiltersAndStorageTest {

    private fun pet(
        id: String,
        name: String,
        species: PetSpecies = PetSpecies.CACHORRO,
        gender: PetGender = PetGender.MACHO,
        favorite: Boolean = false,
        description: String = "Animal dócil"
    ) = Pet(
        id = id, name = name, species = species, gender = gender, age = "2 anos", size = "Porte médio",
        neutered = true, vaccinated = true, description = description, isFavorite = favorite
    )

    private val pets = listOf(
        pet("1", "Mel", gender = PetGender.FEMEA, favorite = true),
        pet("2", "Thor"),
        pet("3", "Zeca", species = PetSpecies.GATO),
        pet("4", "Bia", species = PetSpecies.GATO, gender = PetGender.FEMEA, description = "Adora brincar")
    )

    @Test
    fun filtra_por_especie_e_genero() {
        val gatos = applyFilters(pets, AdotarFilters(species = SpeciesFilter.GATOS, sort = SortOrder.NOME))
        assertEquals(listOf("Bia", "Zeca"), gatos.map { it.name })

        val femeas = applyFilters(pets, AdotarFilters(gender = PetGender.FEMEA, sort = SortOrder.NOME))
        assertEquals(listOf("Bia", "Mel"), femeas.map { it.name })
    }

    @Test
    fun busca_ignora_maiusculas_e_olha_descricao() {
        assertEquals(listOf("Thor"), applyFilters(pets, AdotarFilters(query = "tHoR")).map { it.name })
        assertEquals(listOf("Bia"), applyFilters(pets, AdotarFilters(query = "brincar")).map { it.name })
        assertTrue(applyFilters(pets, AdotarFilters(query = "xyz")).isEmpty())
    }

    @Test
    fun favoritos_e_ordenacao() {
        assertEquals(listOf("Mel"), applyFilters(pets, AdotarFilters(onlyFavorites = true)).map { it.name })
        assertEquals(listOf("Bia", "Mel", "Thor", "Zeca"), applyFilters(pets, AdotarFilters(sort = SortOrder.NOME)).map { it.name })
        // "Mais recentes": o último da lista (cadastrado por último) vem primeiro.
        assertEquals("Bia", applyFilters(pets, AdotarFilters(sort = SortOrder.RECENTES)).first().name)
    }

    @Test
    fun filtros_ativos() {
        assertFalse(AdotarFilters().isActive)
        assertFalse(AdotarFilters(sort = SortOrder.NOME).isActive)
        assertTrue(AdotarFilters(query = "a").isActive)
        assertTrue(AdotarFilters(onlyFavorites = true).isActive)
    }

    @Test
    fun animais_sobrevivem_ao_salvar_e_carregar_json() {
        val original = listOf(
            pet("a", "Rex").copy(photoUris = listOf("content://foto/1", "content://foto/2")),
            pet("b", "Nina").copy(photoUris = emptyList(), description = "Com \"aspas\" e acentuação")
        )
        val restored = PetRepository.parsePets(PetRepository.toJson(original))
        assertEquals(original.map { it.copy(isFavorite = false) }, restored)
    }

    @Test
    fun json_antigo_com_uma_foto_ainda_carrega() {
        val antigo = """[{"id":"x","name":"Old","species":"GATO","gender":"FEMEA","age":"1 ano","size":"Pequeno",
            "neutered":false,"vaccinated":true,"city":"Campinas","state":"SP","description":"d","photoUri":"content://a"}]"""
        val pet = PetRepository.parsePets(antigo).single()
        assertEquals(listOf("content://a"), pet.photoUris)
    }

    @Test
    fun json_antigo_sem_foto() {
        val antigo = """[{"id":"x","name":"Old","species":"GATO","gender":"FEMEA","age":"1 ano","size":"Pequeno",
            "neutered":false,"vaccinated":true,"city":"Campinas","state":"SP","description":"d","photoUri":null}]"""
        val pet = PetRepository.parsePets(antigo).single()
        assertNull(pet.photoUri)
    }

    @Test
    fun historico_sobrevive_ao_salvar_e_carregar_json() {
        val entries = listOf(
            HistoryEntry("1", HistoryType.ADOCAO, "Adoção de Mel", "Cachorro · 2 anos", 1_700_000_000_000),
            HistoryEntry("2", HistoryType.DOACAO, "Doação via PIX", "R$ 25", 1_700_000_100_000)
        )
        assertEquals(entries, HistoryRepository.parse(HistoryRepository.toJson(entries)))
    }

}
