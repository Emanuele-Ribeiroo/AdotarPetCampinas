package com.adotarpet.campinas.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adotarpet.campinas.model.UserProfile

/** Perfil do usuário do aparelho: a tabela tem sempre uma linha só, com id = [SINGLE_ID]. */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = SINGLE_ID,
    val name: String,
    val cpf: String,
    val phone: String,
    val email: String
) {
    companion object {

        const val SINGLE_ID = 0
    }
}

fun UserProfileEntity.toModel(): UserProfile = UserProfile(name, cpf, phone, email)

fun UserProfile.toEntity(): UserProfileEntity =
    UserProfileEntity(name = name, cpf = cpf, phone = phone, email = email)
