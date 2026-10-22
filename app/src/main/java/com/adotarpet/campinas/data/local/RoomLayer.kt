/*
 * Camada Room do Adotar Pet Campinas (MVVM): Entities, Converters, DAOs, Database e Repositories.
 *
 *  View (Compose) → ViewModel → Repository → DAO → AppDatabase (SQLite)
 *
 *  class AdotarPetApplication : Application() {
 *      val petRepository by lazy { PetRepository(AppDatabase.getInstance(this).petDao()) }
 *      ...
 *  }
 *  O ViewModel recebe o repository (via factory) e expõe o Flow como StateFlow com stateIn.
 */
package com.adotarpet.campinas.data.local

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import com.adotarpet.campinas.model.HistoryEntry
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.model.PetGender
import com.adotarpet.campinas.model.PetSpecies
import com.adotarpet.campinas.model.UserProfile
import com.adotarpet.campinas.model.Volunteer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

// ═════════════════════════════════════════════════════════════════════════════
// ENTITIES
// ═════════════════════════════════════════════════════════════════════════════

/** Animal para adoção. [createdAt] preserva a ordem de cadastro ("mais recentes"). */
@Entity(
    tableName = "pets",
    indices = [Index("species"), Index("is_favorite")]
)
data class PetEntity(
    @PrimaryKey val id: String,
    val name: String,
    val species: PetSpecies,
    val gender: PetGender,
    val age: String,
    val size: String,
    val neutered: Boolean,
    val vaccinated: Boolean,
    val city: String = "Campinas",
    val state: String = "SP",
    val description: String,
    @ColumnInfo(name = "photo_res") val photoRes: Int? = null,
    @ColumnInfo(name = "photo_uris") val photoUris: List<String> = emptyList(),
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

/** Perfil do usuário que preenche formulários sozinho. Sempre uma única linha (id = 1). */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = SINGLE_ID,
    val name: String = "",
    val cpf: String = "",
    val phone: String = "",
    val email: String = ""
) {
    companion object {
        const val SINGLE_ID = 1
    }
}

/** Histórico de ações do usuário (adoção, resgate, doação, voluntariado, cadastro). */
@Entity(
    tableName = "history_entries",
    indices = [Index("timestamp")]
)
data class HistoryEntryEntity(
    @PrimaryKey val id: String,
    val type: HistoryType,
    val title: String,
    val subtitle: String,
    val timestamp: Long
)

@Entity(tableName = "volunteers")
data class VolunteerEntity(
    // autoGenerate porque há voluntários com o mesmo nome; o id domínio vira String no mapper.
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

// ═════════════════════════════════════════════════════════════════════════════
// TYPE CONVERTERS
// (enums simples — PetSpecies, PetGender, HistoryType — o Room já grava pelo nome)
// ═════════════════════════════════════════════════════════════════════════════

class Converters {
    @TypeConverter
    fun fromStringList(list: List<String>): String = JSONArray(list).toString()

    @TypeConverter
    fun toStringList(json: String): List<String> {
        if (json.isBlank()) return emptyList()
        val array = JSONArray(json)
        return (0 until array.length()).map { array.getString(it) }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// MAPPERS  (Entity ⇄ modelo de domínio usado pela UI)
// ═════════════════════════════════════════════════════════════════════════════

fun PetEntity.toDomain() = Pet(
    id = id, name = name, species = species, gender = gender, age = age, size = size,
    neutered = neutered, vaccinated = vaccinated, city = city, state = state,
    description = description, photoRes = photoRes, photoUris = photoUris, isFavorite = isFavorite
)

fun Pet.toEntity(createdAt: Long = System.currentTimeMillis()) = PetEntity(
    id = id, name = name, species = species, gender = gender, age = age, size = size,
    neutered = neutered, vaccinated = vaccinated, city = city, state = state,
    description = description, photoRes = photoRes, photoUris = photoUris,
    isFavorite = isFavorite, createdAt = createdAt
)

fun UserProfileEntity.toDomain() = UserProfile(name = name, cpf = cpf, phone = phone, email = email)

fun UserProfile.toEntity() = UserProfileEntity(name = name, cpf = cpf, phone = phone, email = email)

fun HistoryEntryEntity.toDomain() = HistoryEntry(id, type, title, subtitle, timestamp)

fun HistoryEntry.toEntity() = HistoryEntryEntity(id, type, title, subtitle, timestamp)

fun VolunteerEntity.toDomain() = Volunteer(id = id.toString(), name = name)

// ═════════════════════════════════════════════════════════════════════════════
// DAOs
// ═════════════════════════════════════════════════════════════════════════════

@Dao
interface PetDao {
    /** Mais recentes primeiro (a UI pode reordenar por nome). */
    @Query("SELECT * FROM pets ORDER BY created_at DESC")
    fun observeAll(): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets WHERE id = :id")
    fun observeById(id: String): Flow<PetEntity?>

    @Query("SELECT * FROM pets WHERE id = :id")
    suspend fun getById(id: String): PetEntity?

    @Query("SELECT * FROM pets WHERE is_favorite = 1 ORDER BY created_at DESC")
    fun observeFavorites(): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets WHERE species = :species ORDER BY created_at DESC")
    fun observeBySpecies(species: PetSpecies): Flow<List<PetEntity>>

    @Query(
        "SELECT * FROM pets WHERE name LIKE '%' || :text || '%' OR description LIKE '%' || :text || '%' " +
            "OR age LIKE '%' || :text || '%' OR size LIKE '%' || :text || '%' ORDER BY created_at DESC"
    )
    fun search(text: String): Flow<List<PetEntity>>

    @Query("SELECT COUNT(*) FROM pets")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pet: PetEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(pets: List<PetEntity>)

    @Update
    suspend fun update(pet: PetEntity)

    @Query("UPDATE pets SET is_favorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: String, favorite: Boolean)

    @Query("DELETE FROM pets WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = ${UserProfileEntity.SINGLE_ID}")
    fun observe(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = ${UserProfileEntity.SINGLE_ID}")
    suspend fun get(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile")
    suspend fun clear()
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_entries ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<HistoryEntryEntity>>

    @Query("SELECT * FROM history_entries WHERE type = :type ORDER BY timestamp DESC")
    fun observeByType(type: HistoryType): Flow<List<HistoryEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HistoryEntryEntity)

    @Query("DELETE FROM history_entries")
    suspend fun clear()
}

@Dao
interface VolunteerDao {
    @Query("SELECT * FROM volunteers ORDER BY id")
    fun observeAll(): Flow<List<VolunteerEntity>>

    @Query("SELECT COUNT(*) FROM volunteers")
    suspend fun count(): Int

    @Insert
    suspend fun insert(volunteer: VolunteerEntity)

    @Insert
    suspend fun insertAll(volunteers: List<VolunteerEntity>)

    @Query("DELETE FROM volunteers WHERE id = :id")
    suspend fun deleteById(id: Long)
}

// ═════════════════════════════════════════════════════════════════════════════
// DATABASE
// ═════════════════════════════════════════════════════════════════════════════

@Database(
    entities = [
        PetEntity::class,
        UserProfileEntity::class,
        HistoryEntryEntity::class,
        VolunteerEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun historyDao(): HistoryDao
    abstract fun volunteerDao(): VolunteerDao

    companion object {
        private const val DB_NAME = "adotar_pet.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /** Singleton thread-safe. Use o applicationContext para não vazar Activity. */
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    // Ao subir a versão, escreva uma Migration; evite fallbackToDestructiveMigration
                    // porque apagaria os animais cadastrados pelo usuário.
                    .build()
                    .also { INSTANCE = it }
            }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// REPOSITORIES  (única porta de entrada de dados para os ViewModels)
// ═════════════════════════════════════════════════════════════════════════════

class PetRepository(private val dao: PetDao) {

    val pets: Flow<List<Pet>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    val favorites: Flow<List<Pet>> = dao.observeFavorites().map { list -> list.map { it.toDomain() } }

    fun observePet(id: String): Flow<Pet?> = dao.observeById(id).map { it?.toDomain() }

    fun search(text: String): Flow<List<Pet>> =
        dao.search(text.trim()).map { list -> list.map { it.toDomain() } }

    fun bySpecies(species: PetSpecies): Flow<List<Pet>> =
        dao.observeBySpecies(species).map { list -> list.map { it.toDomain() } }

    suspend fun petById(id: String): Pet? = dao.getById(id)?.toDomain()

    suspend fun addPet(pet: Pet) = dao.insert(pet.toEntity())

    /** Mantém a data de cadastro original para não mudar a ordem da lista. */
    suspend fun updatePet(pet: Pet) {
        val createdAt = dao.getById(pet.id)?.createdAt ?: System.currentTimeMillis()
        dao.update(pet.toEntity(createdAt))
    }

    suspend fun deletePet(id: String) = dao.deleteById(id)

    suspend fun toggleFavorite(id: String) {
        val current = dao.getById(id) ?: return
        dao.setFavorite(id, !current.isFavorite)
    }

    /** Carga inicial: grava os animais de exemplo só se a tabela estiver vazia. */
    suspend fun seedIfEmpty(initial: List<Pet>) {
        if (dao.count() > 0) return
        val now = System.currentTimeMillis()
        // createdAt decrescente preserva a ordem original da lista de exemplo.
        dao.insertAll(initial.mapIndexed { i, pet -> pet.toEntity(createdAt = now - i) }.reversed())
    }
}

class UserProfileRepository(private val dao: UserProfileDao) {

    val profile: Flow<UserProfile> = dao.observe().map { it?.toDomain() ?: UserProfile() }

    suspend fun save(profile: UserProfile) = dao.upsert(profile.toEntity())

    /** Preenche só os campos que o usuário ainda não tinha informado. */
    suspend fun fillBlanks(name: String = "", cpf: String = "", phone: String = "", email: String = "") {
        val p = dao.get()?.toDomain() ?: UserProfile()
        save(
            p.copy(
                name = p.name.ifBlank { name },
                cpf = p.cpf.ifBlank { cpf },
                phone = p.phone.ifBlank { phone },
                email = p.email.ifBlank { email }
            )
        )
    }

    suspend fun clear() = dao.clear()
}

class HistoryRepository(private val dao: HistoryDao) {

    val entries: Flow<List<HistoryEntry>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    fun byType(type: HistoryType): Flow<List<HistoryEntry>> =
        dao.observeByType(type).map { list -> list.map { it.toDomain() } }

    suspend fun add(type: HistoryType, title: String, subtitle: String = "") {
        dao.insert(
            HistoryEntry(
                id = java.util.UUID.randomUUID().toString(),
                type = type,
                title = title,
                subtitle = subtitle,
                timestamp = System.currentTimeMillis()
            ).toEntity()
        )
    }

    suspend fun clear() = dao.clear()
}

class VolunteerRepository(private val dao: VolunteerDao) {

    val volunteers: Flow<List<Volunteer>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun add(name: String) = dao.insert(VolunteerEntity(name = name))

    suspend fun remove(id: String) = id.toLongOrNull()?.let { dao.deleteById(it) }

    suspend fun seedIfEmpty(names: List<String>) {
        if (dao.count() > 0) return
        dao.insertAll(names.map { VolunteerEntity(name = it) })
    }
}
