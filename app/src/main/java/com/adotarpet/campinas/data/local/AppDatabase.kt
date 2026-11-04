package com.adotarpet.campinas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.adotarpet.campinas.data.local.dao.HistoryDao
import com.adotarpet.campinas.data.local.dao.PetDao
import com.adotarpet.campinas.data.local.dao.UserProfileDao
import com.adotarpet.campinas.data.local.entity.HistoryEntryEntity
import com.adotarpet.campinas.data.local.entity.PetEntity
import com.adotarpet.campinas.data.local.entity.PetPhotoEntity
import com.adotarpet.campinas.data.local.entity.UserProfileEntity

/**
 * Banco local do app. Ao mudar alguma entidade, suba [version] e escreva uma Migration
 * (o esquema de cada versão fica salvo em app/schemas/ para comparar).
 */
@Database(
    entities = [
        PetEntity::class,
        PetPhotoEntity::class,
        HistoryEntryEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun historyDao(): HistoryDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "adotar_pet.db"
            ).build().also { instance = it }
        }
    }
}
