package com.adotarpet.campinas.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.adotarpet.campinas.repository.local.dao.HistoryDao
import com.adotarpet.campinas.repository.local.dao.PetDao
import com.adotarpet.campinas.repository.local.dao.UserProfileDao
import com.adotarpet.campinas.repository.local.entity.HistoryEntryEntity
import com.adotarpet.campinas.repository.local.entity.PetEntity
import com.adotarpet.campinas.repository.local.entity.PetPhotoEntity
import com.adotarpet.campinas.repository.local.entity.UserProfileEntity

@Database(
    entities = [
        PetEntity::class,
        PetPhotoEntity::class,
        HistoryEntryEntity::class,
        UserProfileEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun historyDao(): HistoryDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE history ADD COLUMN status TEXT NOT NULL DEFAULT 'EM_ANALISE'")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE history ADD COLUMN details TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE history ADD COLUMN motivoCancelamento TEXT NOT NULL DEFAULT ''")
            }
        }

        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "adotar_pet.db"
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build().also { instance = it }
        }
    }
}
