package com.adotarpet.campinas.repository

import com.adotarpet.campinas.repository.remote.ViaCepApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class Endereco(
    val logradouro: String,
    val bairro: String,
    val cidade: String,
    val uf: String
)

sealed interface CepResultado {
    data class Encontrado(val endereco: Endereco) : CepResultado
    data object Invalido : CepResultado
    data object ErroRede : CepResultado
}

/** Esconde do ViewModel de onde vem o endereço (hoje, a API do ViaCEP via Retrofit). */
object CepRepository {

    private val api by lazy { ViaCepApi.create() }

    suspend fun buscar(cep: String): CepResultado = withContext(Dispatchers.IO) {
        val digitos = cep.filter { it.isDigit() }
        if (digitos.length != 8) return@withContext CepResultado.Invalido

        try {
            val resposta = api.buscarCep(digitos)
            if (resposta.erro == true) {
                CepResultado.Invalido
            } else {
                CepResultado.Encontrado(
                    Endereco(
                        logradouro = resposta.logradouro.orEmpty(),
                        bairro = resposta.bairro.orEmpty(),
                        cidade = resposta.cidade.orEmpty(),
                        uf = resposta.uf.orEmpty()
                    )
                )
            }
        } catch (e: Exception) {
            // Sem internet, timeout ou resposta inesperada: a tela segue funcionando sem o preenchimento automático.
            CepResultado.ErroRede
        }
    }
}
