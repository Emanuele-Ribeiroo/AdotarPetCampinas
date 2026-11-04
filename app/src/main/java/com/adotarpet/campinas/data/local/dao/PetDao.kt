package com.adotarpet.campinas.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.adotarpet.campinas.data.local.entity.PetEntity
import com.adotarpet.campinas.data.local.entity.PetPhotoEntity
import com.adotarpet.campinas.data.local.entity.PetWithPhotos
import com.adotarpet.campinas.model.PetSpecies
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {

    // rowid = ordem de cadastro; a tela Adotar inverte para mostrar os mais recentes primeiro.
    @Transaction
    @Query("SELECT * FROM pets ORDER BY rowid")
    fun observeAll(): Flow<List<PetWithPhotos>>

    @Transaction
    @Query("SELECT * FROM pets WHERE id = :id")
    suspend fun getById(id: String): PetWithPhotos?

    @Transaction
    @Query("SELECT * FROM pets WHERE isFavorite = 1 ORDER BY rowid")
    fun observeFavorites(): Flow<List<PetWithPhotos>>

    @Transaction
    @Query(
        """
        SELECT * FROM pets
        WHERE (:species IS NULL OR species = :species)
          AND (:state IS NULL OR state = :state)
        ORDER BY rowid
        """
    )
    fun observeFiltered(species: PetSpecies?, state: String?): Flow<List<PetWithPhotos>>

    @Query("SELECT COUNT(*) FROM pets")
    suspend fun count(): Int

    // @Upsert faz UPDATE quando o id já existe (sem apagar a linha), então as fotos não somem pelo CASCADE.
    @Upsert
    suspend fun upsertPet(pet: PetEntity)

    @Insert
    suspend fun insertPhotos(photos: List<PetPhotoEntity>)

    @Query("DELETE FROM pet_photos WHERE petId = :petId")
    suspend fun deletePhotos(petId: String)

    @Query("DELETE FROM pets WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE pets SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: String)

    /** Grava o animal e troca a lista de fotos inteira, tudo numa transação só. */
    @Transaction
    suspend fun savePetWithPhotos(pet: PetEntity, photos: List<PetPhotoEntity>) {
        upsertPet(pet)
        deletePhotos(pet.id)
        if (photos.isNotEmpty()) insertPhotos(photos)
    }
}
