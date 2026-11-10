package com.adotarpet.campinas.repository.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/** Resposta do ViaCEP. O Gson converte o JSON da API direto para esta data class. */
data class ViaCepResponse(
    @SerializedName("logradouro") val logradouro: String?,
    @SerializedName("bairro") val bairro: String?,
    @SerializedName("localidade") val cidade: String?,
    @SerializedName("uf") val uf: String?,
    // O ViaCEP devolve { "erro": true } quando o CEP tem 8 dígitos mas não existe.
    @SerializedName("erro") val erro: Boolean?
)

interface ViaCepApi {

    @GET("ws/{cep}/json/")
    suspend fun buscarCep(@Path("cep") cep: String): ViaCepResponse

    companion object {
        private const val BASE_URL = "https://viacep.com.br/"

        fun create(): ViaCepApi =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ViaCepApi::class.java)
    }
}
