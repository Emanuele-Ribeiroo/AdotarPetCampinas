package com.adotarpet.campinas.repository

import com.adotarpet.campinas.repository.remote.AdoptionApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Envia as solicitações de adoção (e seus cancelamentos) para a planilha da ONG. */
object AdoptionRepository {

    private val api by lazy { AdoptionApi.create() }

    suspend fun enviarFormulario(dados: Map<String, String>): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val resposta = api.enviarFormulario(dados)
                if (resposta.isSuccessful) {
                    Result.success(resposta.body()?.string().orEmpty())
                } else {
                    Result.failure(Exception("HTTP ${resposta.code()}: ${resposta.errorBody()?.string().orEmpty()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
