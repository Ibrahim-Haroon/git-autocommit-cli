package com.ibrahimharoon.gitautocommit.llm.provider

import com.ibrahimharoon.gitautocommit.llm.LlmType
import com.ibrahimharoon.gitautocommit.llm.registry.LlmRegistry
import com.ibrahimharoon.gitautocommit.llm.service.GoogleVertexLlmResponseService
import com.ibrahimharoon.gitautocommit.llm.service.LlmResponseService
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.http.HttpHeaders
import java.io.BufferedReader
import java.io.InputStreamReader

@LlmRegistry(LlmType.GOOGLE_VERTEX)
class GoogleVertexLlmProvider : DefaultLlmProvider() {
    override val model = "gemini-1.5-flash"

    private val dotenv = Dotenv.configure()
        .directory(System.getProperty("user.home") + "/.local/bin")
        .filename("autocommit-config.env")
        .load()
    private val PROJECT_ID = dotenv["GOOGLE_VERTEX_PROJECT_ID"]
    private val LOCATION = dotenv["GOOGLE_VERTEX_LOCATION"]

    override val url = "https://us-central1-aiplatform.googleapis.com/v1/projects/$PROJECT_ID/locations/$LOCATION/publishers/google/models/$model:generateContent"

    private val headers = HttpHeaders().apply {
        set("Content-Type", "application/json")
        set("Authorization", "Bearer ${getAuthToken()}")
    }

    override val responseService: LlmResponseService = GoogleVertexLlmResponseService(url, headers)

    private fun getAuthToken(): String {
        val processBuilder = ProcessBuilder()
        processBuilder.command("gcloud", "auth", "print-access-token")

        return try {
            val process = processBuilder.start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val result = reader.readLine()
            process.waitFor()
            reader.close()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}