package com.adotarpet.campinas.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adotarpet.campinas.data.PetDataSource
import com.adotarpet.campinas.data.PetRepository
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.model.PetGender
import com.adotarpet.campinas.model.PetSpecies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

enum class SpeciesFilter(val label: String) {
    TODOS("Todos"), CACHORROS("Cachorros"), GATOS("Gatos")
}

enum class SortOrder(val label: String) {
    RECENTES("Mais recentes"), NOME("Nome (A–Z)")
}

data class AdotarFilters(
    val query: String = "",
    val species: SpeciesFilter = SpeciesFilter.TODOS,
    val gender: PetGender? = null,
    val onlyFavorites: Boolean = false,
    val sort: SortOrder = SortOrder.RECENTES
) {
    /** Só conta busca e filtros; a ordenação não "filtra" nada. */
    val isActive: Boolean
        get() = query.isNotBlank() || species != SpeciesFilter.TODOS || gender != null || onlyFavorites
}

/** Aplica busca, filtros e ordenação a uma lista de animais. */
fun applyFilters(pets: List<Pet>, f: AdotarFilters): List<Pet> {
    val text = f.query.trim()
    val filtered = pets.filter { pet ->
        val speciesOk = when (f.species) {
            SpeciesFilter.TODOS -> true
            SpeciesFilter.CACHORROS -> pet.species == PetSpecies.CACHORRO
            SpeciesFilter.GATOS -> pet.species == PetSpecies.GATO
        }
        val queryOk = text.isEmpty() || listOf(pet.name, pet.age, pet.size, pet.description)
            .any { it.contains(text, ignoreCase = true) }
        speciesOk && queryOk &&
            (f.gender == null || pet.gender == f.gender) &&
            (!f.onlyFavorites || pet.isFavorite)
    }
    return when (f.sort) {
        SortOrder.RECENTES -> filtered.reversed() // quem foi cadastrado por último aparece primeiro
        SortOrder.NOME -> filtered.sortedBy { it.name.lowercase() }
    }
}

/** Guarda busca e filtros da lista de adoção, para sobreviverem à navegação entre telas. */
class AdotarViewModel(
    private val repository: PetDataSource = PetRepository
) : ViewModel() {

    private val _filters = MutableStateFlow(AdotarFilters())
    val filters: StateFlow<AdotarFilters> = _filters

    val pets: StateFlow<List<Pet>> = combine(repository.pets, _filters) { pets, f ->
        applyFilters(pets, f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setQuery(query: String) = _filters.update { it.copy(query = query) }
    fun setSpecies(species: SpeciesFilter) = _filters.update { it.copy(species = species) }
    fun setSort(sort: SortOrder) = _filters.update { it.copy(sort = sort) }
    fun toggleGender(gender: PetGender) =
        _filters.update { it.copy(gender = if (it.gender == gender) null else gender) }
    fun toggleFavorites() = _filters.update { it.copy(onlyFavorites = !it.onlyFavorites) }
    fun clearFilters() = _filters.update { AdotarFilters(sort = it.sort) }

    fun toggleFavorite(id: String) = repository.toggleFavorite(id)
}
