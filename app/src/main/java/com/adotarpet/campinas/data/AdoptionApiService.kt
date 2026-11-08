package com.adotarpet.campinas.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Envia os dados do formulário de adoção para o Web App do Google Apps Script,
 * que grava uma linha na planilha. Toda a rede roda em Dispatchers.IO (fora da UI).
 */
object AdoptionApiService {

    // URL de implantação (/exec) do Web App do Apps Script.
    private const val WEB_APP_URL =
        "https://script.google.com/macros/s/AKfycbyKvqBZjDPpW0D2lQs-ShV5pnAL5X_HUoWT7D2e5sblVn6a6hCR_jrIEWcpyTN2Q0qtRw/exec"

    /**
     * @param dados pares chave→valor já prontos (as chaves batem com o que o doPost lê).
     * @return Result.success com o corpo da resposta, ou Result.failure com o erro.
     */
    suspend fun enviarFormulario(dados: Map<String, String>): Result<String> =
        withContext(Dispatchers.IO) {
            var conn: HttpURLConnection? = null
            try {
                val json = JSONObject()
                dados.forEach { (chave, valor) -> json.put(chave, valor) }

                conn = (URL(WEB_APP_URL).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    // O Apps Script responde com um 302 para um domínio googleusercontent;
                    // precisamos seguir esse redirecionamento para ler a resposta.
                    instanceFollowRedirects = true
                    connectTimeout = 15_000
                    readTimeout = 15_000
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                }

                conn.outputStream.use { it.write(json.toString().toByteArray(Charsets.UTF_8)) }

                val code = conn.responseCode
                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                val body = stream?.bufferedReader()?.use { it.readText() }.orEmpty()

                if (code in 200..299) Result.success(body)
                else Result.failure(Exception("HTTP $code: $body"))
            } catch (e: Exception) {
                Result.failure(e)
            } finally {
                conn?.disconnect()
            }
        }
}
