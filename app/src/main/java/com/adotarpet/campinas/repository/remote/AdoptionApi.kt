package com.adotarpet.campinas.repository.remote

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

/** Web App (Google Apps Script) que grava as solicitações de adoção na planilha da ONG. */
interface AdoptionApi {

    // O Gson transforma o Map em um objeto JSON no corpo do POST.
    @POST(WEB_APP_PATH)
    suspend fun enviarFormulario(@Body dados: Map<String, String>): Response<ResponseBody>

    companion object {
        private const val BASE_URL = "https://script.google.com/"
        private const val WEB_APP_PATH =
            "macros/s/AKfycbyKvqBZjDPpW0D2lQs-ShV5pnAL5X_HUoWT7D2e5sblVn6a6hCR_jrIEWcpyTN2Q0qtRw/exec"

        fun create(): AdoptionApi {
            // O Apps Script pode demorar para responder; mantém os 15 s que o app já usava.
            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AdoptionApi::class.java)
        }
    }
}
