package com.adotarpet.campinas.data

import android.content.Context
import android.content.SharedPreferences
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.model.PetGender
import com.adotarpet.campinas.model.PetSpecies
import com.adotarpet.campinas.model.Volunteer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject

/**
 * Fonte de dados mock em memória. Troque por uma chamada de API / Room quando
 * o backend do app estiver definido — a UI já consome tudo via StateFlow,
 * então basta substituir a implementação interna.
 */
object PetRepository : PetDataSource {

    private val initialPets = listOf(
        Pet(
            id = "mel",
            name = "Mel",
            species = PetSpecies.CACHORRO,
            gender = PetGender.FEMEA,
            age = "2 anos",
            size = "Porte médio",
            neutered = true,
            vaccinated = true,
            description = "Mel chegou muito magrinha e hoje é a mais brincalhona do lar temporário. " +
                "Adora criança, aceita outros cachorros e já sabe passear na guia."
        ),
        Pet(
            id = "bento",
            name = "Bento",
            species = PetSpecies.CACHORRO,
            gender = PetGender.MACHO,
            age = "4 anos",
            size = "Porte grande",
            neutered = true,
            vaccinated = true,
            description = "Bento é calmo, ótimo de apartamento e adora um cochilo no sofá. " +
                "Já é castrado e está com a vacinação em dia."
        ),
        Pet(
            id = "tobias",
            name = "Tobias",
            species = PetSpecies.GATO,
            gender = PetGender.MACHO,
            age = "8 meses",
            size = "Porte pequeno",
            neutered = true,
            vaccinated = true,
            description = "Tobias é curioso e cheio de energia, adora brincar com bolinhas de papel " +
                "e já está usando caixa de areia sem problema nenhum."
        ),
        Pet(
            id = "zeca",
            name = "Zeca",
            species = PetSpecies.GATO,
            gender = PetGender.MACHO,
            age = "3 anos",
            size = "Porte médio",
            neutered = true,
            vaccinated = true,
            description = "Zeca é tranquilo, independente e ronrona fácil. Ideal para quem quer " +
                "companhia sem muita bagunça."
        )
    )

    private val _pets = MutableStateFlow(initialPets)
    override val pets: StateFlow<List<Pet>> = _pets

    private var prefs: SharedPreferences? = null

    /**
     * Carrega do aparelho os animais cadastrados e os favoritos. Chamado uma vez em
     * [com.adotarpet.campinas.AdotarPetApplication]; sem isso, tudo fica só em memória.
     */
    fun init(context: Context) {
        if (prefs != null) return
        val p = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs = p

        val favoriteIds = p.getStringSet(KEY_FAVORITES, emptySet()).orEmpty()
        val saved = p.getString(KEY_ALL, null)
        val base = if (saved != null) {
            runCatching { parsePets(saved) }.getOrDefault(initialPets)
        } else {
            // Versão antiga: só os animais cadastrados eram guardados, somados aos de exemplo.
            initialPets + runCatching { parsePets(p.getString(KEY_REGISTERED, null)) }.getOrDefault(emptyList())
        }
        _pets.value = base.map { it.copy(isFavorite = it.id in favoriteIds) }
    }

    private fun persist() {
        val p = prefs ?: return
        val all = _pets.value
        p.edit()
            .putString(KEY_ALL, toJson(all))
            .putStringSet(KEY_FAVORITES, all.filter { it.isFavorite }.map { it.id }.toSet())
            .apply()
    }

    internal fun toJson(pets: List<Pet>): String = JSONArray().apply {
        pets.forEach { pet ->
            put(
                JSONObject()
                    .put("id", pet.id)
                    .put("name", pet.name)
                    .put("species", pet.species.name)
                    .put("gender", pet.gender.name)
                    .put("age", pet.age)
                    .put("size", pet.size)
                    .put("neutered", pet.neutered)
                    .put("vaccinated", pet.vaccinated)
                    .put("city", pet.city)
                    .put("state", pet.state)
                    .put("description", pet.description)
                    .put("photoUris", JSONArray(pet.photoUris))
            )
        }
    }.toString()

    internal fun parsePets(json: String?): List<Pet> {
        if (json.isNullOrBlank()) return emptyList()
        val array = JSONArray(json)
        return (0 until array.length()).map { i ->
            val o = array.getJSONObject(i)
            Pet(
                id = o.getString("id"),
                name = o.getString("name"),
                species = PetSpecies.valueOf(o.getString("species")),
                gender = PetGender.valueOf(o.getString("gender")),
                age = o.getString("age"),
                size = o.getString("size"),
                neutered = o.getBoolean("neutered"),
                vaccinated = o.getBoolean("vaccinated"),
                city = o.getString("city"),
                state = o.getString("state"),
                description = o.getString("description"),
                photoUris = parsePhotos(o)
            )
        }
    }

    private const val PREFS_NAME = "adotar_pet"
    private const val KEY_REGISTERED = "registered_pets" // formato antigo, só para migrar
    private const val KEY_ALL = "all_pets"
    private const val KEY_FAVORITES = "favorite_ids"

    val volunteers = listOf(
        Volunteer("1", "João"),
        Volunteer("2", "Rafaela"),
        Volunteer("3", "João"),
    )

    override fun petById(id: String): Pet? = _pets.value.find { it.id == id }

    /** Adiciona um novo animal cadastrado (ex.: pela tela "Cadastrar animal"). */
    override fun addPet(pet: Pet) {
        _pets.update { list -> list + pet }
        persist()
    }

    override fun toggleFavorite(id: String) {
        _pets.update { list ->
            list.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it }
        }
        persist()
    }

    fun favorites(): List<Pet> = _pets.value.filter { it.isFavorite }

    override fun updatePet(pet: Pet) {
        _pets.update { list -> list.map { if (it.id == pet.id) pet else it } }
        persist()
    }

    override fun deletePet(id: String) {
        _pets.update { list -> list.filterNot { it.id == id } }
        persist()
    }

    private fun parsePhotos(o: JSONObject): List<String> {
        val array = o.optJSONArray("photoUris")
        if (array != null) return (0 until array.length()).map { array.getString(it) }
        // Formato antigo: uma única foto em "photoUri".
        return if (o.isNull("photoUri") || !o.has("photoUri")) emptyList() else listOf(o.getString("photoUri"))
    }
}
