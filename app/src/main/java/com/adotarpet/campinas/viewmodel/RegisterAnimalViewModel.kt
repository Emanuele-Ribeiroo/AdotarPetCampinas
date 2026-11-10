package com.adotarpet.campinas.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.adotarpet.campinas.model.Attachment
import com.adotarpet.campinas.model.HistoryType
import com.adotarpet.campinas.model.Pet
import com.adotarpet.campinas.model.PetGender
import com.adotarpet.campinas.model.PetSpecies
import com.adotarpet.campinas.repository.HistoryRepository
import com.adotarpet.campinas.repository.PetRepository
import java.util.UUID

/** Estado e regras do cadastro/edição de animal. Com [petId] nulo, é um cadastro novo. */
class RegisterAnimalViewModel(petId: String?) : ViewModel() {

    private val existing: Pet? = petId?.let { PetRepository.petById(it) }
    val editando: Boolean = existing != null

    var nome by mutableStateOf(existing?.name.orEmpty())
    var especie by mutableStateOf(existing?.species ?: PetSpecies.CACHORRO)
    var genero by mutableStateOf(existing?.gender ?: PetGender.MACHO)
    var idade by mutableStateOf(existing?.age.orEmpty())
    var porte by mutableStateOf(existing?.size.orEmpty())
    var castrado by mutableStateOf(existing?.neutered ?: false)
    var vacinado by mutableStateOf(existing?.vaccinated ?: false)
    var descricao by mutableStateOf(existing?.description.orEmpty())
    var anexos by mutableStateOf(
        existing?.photoUris.orEmpty().mapIndexed { i, uri -> Attachment(Uri.parse(uri), "Foto ${i + 1}") }
    )

    val podeSalvar: Boolean get() = nome.isNotBlank()

    /** Só pede confirmação para descartar em cadastro novo. */
    val temAlteracoes: Boolean
        get() = !editando && (
            nome.isNotBlank() || idade.isNotBlank() || porte.isNotBlank() ||
                descricao.isNotBlank() || castrado || vacinado || anexos.isNotEmpty()
            )

    /**
     * Salva o animal. [ehImagem] diz se um anexo novo é foto (a tela consulta o Android);
     * vídeos não entram na galeria do animal.
     */
    fun salvar(ehImagem: (Uri) -> Boolean) {
        if (!podeSalvar) return
        val fotosAntigas = existing?.photoUris.orEmpty()
        val fotos = anexos
            .filter { it.uri.toString() in fotosAntigas || ehImagem(it.uri) }
            .map { it.uri.toString() }

        if (existing != null) {
            PetRepository.updatePet(
                existing.copy(
                    name = nome.trim(),
                    species = especie,
                    gender = genero,
                    age = idade.trim().ifBlank { "Idade não informada" },
                    size = porte.trim().ifBlank { "Porte não informado" },
                    neutered = castrado,
                    vaccinated = vacinado,
                    description = descricao.trim(),
                    photoUris = fotos
                )
            )
        } else {
            val novo = Pet(
                id = UUID.randomUUID().toString(),
                name = nome.trim(),
                species = especie,
                gender = genero,
                age = idade.trim().ifBlank { "Idade não informada" },
                size = porte.trim().ifBlank { "Porte não informado" },
                neutered = castrado,
                vaccinated = vacinado,
                description = descricao.trim(),
                photoUris = fotos
            )
            PetRepository.addPet(novo)
            HistoryRepository.add(HistoryType.CADASTRO, "Cadastro de ${novo.name}", "${novo.speciesLabel} · ${novo.age}")
        }
    }
}
