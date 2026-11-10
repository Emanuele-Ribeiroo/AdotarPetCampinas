package com.adotarpet.campinas.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.room.withTransaction
import com.adotarpet.campinas.R
import com.adotarpet.campinas.repository.local.AppDatabase
import com.adotarpet.campinas.repository.local.dao.PetDao
import com.adotarpet.campinas.repository.local.entity.toEntity
import com.adotarpet.campinas.repository.local.entity.toModel
import com.adotarpet.campinas.repository.local.entity.toPhotoEntities
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.model.PetGender
import com.adotarpet.campinas.model.PetSpecies
import com.adotarpet.campinas.model.Volunteer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

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

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    override val pets: StateFlow<List<Pet>> = _pets

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var dao: PetDao? = null

    fun init(context: Context) {
        if (dao != null) return
        val db = AppDatabase.get(context)
        val petDao = db.petDao()
        dao = petDao
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        scope.launch {
            seedOnce(db, petDao, prefs)
            petDao.observeAll().collect { list ->
                _pets.value = list.map { entity ->
                    val pet = entity.toModel()

                    if (pet.photoUris.isEmpty()) pet.copy(photoRes = R.drawable.pet_filhote) else pet
                }
            }
        }
    }

    private suspend fun seedOnce(db: AppDatabase, petDao: PetDao, prefs: SharedPreferences) {
        if (prefs.getBoolean(KEY_ROOM_SEEDED, false)) return
        val favoriteIds = prefs.getStringSet(KEY_FAVORITES, emptySet()).orEmpty()
        val saved = prefs.getString(KEY_ALL, null)
        val base = if (saved != null) {
            runCatching { parsePets(saved) }.getOrDefault(initialPets)
        } else {

            initialPets + runCatching { parsePets(prefs.getString(KEY_REGISTERED, null)) }.getOrDefault(emptyList())
        }
        db.withTransaction {
            base.forEach { pet ->
                val withFavorite = pet.copy(isFavorite = pet.id in favoriteIds)
                petDao.savePetWithPhotos(withFavorite.toEntity(), withFavorite.toPhotoEntities())
            }
        }
        prefs.edit()
            .putBoolean(KEY_ROOM_SEEDED, true)
            .remove(KEY_ALL)
            .remove(KEY_REGISTERED)
            .remove(KEY_FAVORITES)
            .apply()
    }

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
    private const val KEY_REGISTERED = "registered_pets"
    private const val KEY_ALL = "all_pets"
    private const val KEY_FAVORITES = "favorite_ids"
    private const val KEY_ROOM_SEEDED = "room_seeded"

    val volunteers = listOf(
        Volunteer("1", "João", photoRes = R.drawable.person_woman),
        Volunteer("2", "Rafaela", photoRes = R.drawable.person_woman),
        Volunteer("3", "João", photoRes = R.drawable.person_woman),
    )

    override fun petById(id: String): Pet? = _pets.value.find { it.id == id }

    override fun addPet(pet: Pet) = write { savePetWithPhotos(pet.toEntity(), pet.toPhotoEntities()) }

    override fun toggleFavorite(id: String) = write { toggleFavorite(id) }

    fun favorites(): List<Pet> = _pets.value.filter { it.isFavorite }

    override fun updatePet(pet: Pet) = write { savePetWithPhotos(pet.toEntity(), pet.toPhotoEntities()) }

    override fun deletePet(id: String) = write { deleteById(id) }

    private fun write(block: suspend PetDao.() -> Unit) {
        val petDao = dao ?: return
        scope.launch { petDao.block() }
    }

    private fun parsePhotos(o: JSONObject): List<String> {
        val array = o.optJSONArray("photoUris")
        if (array != null) return (0 until array.length()).map { array.getString(it) }

        return if (o.isNull("photoUri") || !o.has("photoUri")) emptyList() else listOf(o.getString("photoUri"))
    }
}
