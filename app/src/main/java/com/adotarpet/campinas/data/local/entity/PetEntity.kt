package com.adotarpet.campinas.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.model.PetGender
import com.adotarpet.campinas.model.PetSpecies

/**
 * Tabela de animais. O id continua texto ("mel", UUID...) porque ele já é usado nas rotas de
 * navegação ("pet/{petId}"). Enums são gravados pelo nome (Room converte sozinho).
 */
@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey val id: String,
    val name: String,
    val species: PetSpecies,
    val gender: PetGender,
    val age: String,
    val size: String,
    val neutered: Boolean,
    val vaccinated: Boolean,
    val city: String,
    val state: String,
    val description: String,
    val isFavorite: Boolean = false
)

/** Fotos de cada animal; apagar o animal apaga as fotos junto (CASCADE). */
@Entity(
    tableName = "pet_photos",
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("petId")]
)
data class PetPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val petId: String,
    val uri: String,       // content:// da galeria ou URL
    val position: Int      // ordem da foto; a primeira aparece nos cards
)

/** Animal com as fotos, montado pelo Room com @Relation. */
data class PetWithPhotos(
    @Embedded val pet: PetEntity,
    @Relation(parentColumn = "id", entityColumn = "petId")
    val photos: List<PetPhotoEntity>
)

fun PetWithPhotos.toModel(): Pet = Pet(
    id = pet.id,
    name = pet.name,
    species = pet.species,
    gender = pet.gender,
    age = pet.age,
    size = pet.size,
    neutered = pet.neutered,
    vaccinated = pet.vaccinated,
    city = pet.city,
    state = pet.state,
    description = pet.description,
    photoUris = photos.sortedBy { it.position }.map { it.uri },
    isFavorite = pet.isFavorite
)

fun Pet.toEntity(): PetEntity = PetEntity(
    id = id,
    name = name,
    species = species,
    gender = gender,
    age = age,
    size = size,
    neutered = neutered,
    vaccinated = vaccinated,
    city = city,
    state = state,
    description = description,
    isFavorite = isFavorite
)

fun Pet.toPhotoEntities(): List<PetPhotoEntity> =
    photoUris.mapIndexed { index, uri -> PetPhotoEntity(petId = id, uri = uri, position = index) }
