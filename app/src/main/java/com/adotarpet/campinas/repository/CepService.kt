package com.adotarpet.campinas.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

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

object CepService {

    suspend fun buscar(cep: String): CepResultado = withContext(Dispatchers.IO) {
        val digitos = cep.filter { it.isDigit() }
        if (digitos.length != 8) return@withContext CepResultado.Invalido

        var conn: HttpURLConnection? = null
        try {
            conn = (URL("https://viacep.com.br/ws/$digitos/json/").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
            }
            if (conn.responseCode !in 200..299) return@withContext CepResultado.ErroRede
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(body)

            if (json.optBoolean("erro", false)) return@withContext CepResultado.Invalido

            CepResultado.Encontrado(
                Endereco(
                    logradouro = json.optString("logradouro", ""),
                    bairro = json.optString("bairro", ""),
                    cidade = json.optString("localidade", ""),
                    uf = json.optString("uf", "")
                )
            )
        } catch (e: Exception) {
            CepResultado.ErroRede
        } finally {
            conn?.disconnect()
        }
    }
}
