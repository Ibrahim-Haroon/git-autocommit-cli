package com.ibrahimharoon.gitautocommit.llm.provider

import com.ibrahimharoon.gitautocommit.llm.LlmType.GOOGLE_VERTEX
import com.ibrahimharoon.gitautocommit.llm.registry.LlmRegistry
import com.ibrahimharoon.gitautocommit.llm.response.LlmResponse
import com.ibrahimharoon.gitautocommit.llm.service.GoogleVertexLlmResponseService
import com.ibrahimharoon.gitautocommit.templates.LlmPromptContextualizer
import com.ibrahimharoon.gitautocommit.templates.LlmTemplates.Companion.ROLE
import io.github.cdimascio.dotenv.Dotenv
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import java.io.BufferedReader
import java.io.InputStreamReader

@LlmRegistry(GOOGLE_VERTEX)
object GoogleVertexLlmResponse: LlmResponse {
    private val logger = LoggerFactory.getLogger(GoogleVertexLlmResponse::class.java)
    private val dotenv = Dotenv.configure()
        .directory(System.getProperty("user.home") + "/.local/bin")
        .filename("autocommit-config.env")
        .load()
    private val PROJECT_ID = dotenv["GOOGLE_VERTEX_PROJECT_ID"]
    private val LOCATION = dotenv["GOOGLE_VERTEX_LOCATION"]
    private const val MODEL = "gemini-1.5-flash"
    private const val BASE_URL = "https://us-central1-aiplatform.googleapis.com/v1/projects"
    private val URL = "$BASE_URL/$PROJECT_ID/locations/$LOCATION/publishers/google/models/$MODEL:generateContent"

    private val headers = HttpHeaders().apply {
        set("Content-Type", "application/json")
        set("Authorization", "Bearer ${getAuthToken()}")
    }

    private val llmResponse = GoogleVertexLlmResponseService(
        URL,
        headers
    )

    override fun getMessage(gitData: String, isPr: Boolean): String {
        try {
            logger.debug("Using Google Vertex model to generate commit message")
            val prompt = LlmPromptContextualizer.generate(gitData, isPr)
            return llmResponse.response(ROLE, prompt)
        } catch (e: Exception) {
            logger.error("Error generating commit message", e)
            return "Error generating Google Vertex commit message - make sure your API key is valid/set"
        }
    }

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
