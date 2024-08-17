package com.ibrahimharoon.gitautocommit.llm.provider

import com.ibrahimharoon.gitautocommit.cli.CliConfigManager
import com.ibrahimharoon.gitautocommit.llm.service.GoogleVertexLlmResponseService
import com.ibrahimharoon.gitautocommit.llm.service.LlmResponseService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.system.exitProcess

class GoogleVertexLlmProvider : DefaultLlmProvider() {
    override val model = "gemini-1.5-flash"
    private val baseUrl = "https://us-central1-aiplatform.googleapis.com/v1/projects"
    private val projectId = CliConfigManager["GOOGLE_VERTEX_PROJECT_ID"]
    private val location = CliConfigManager["GOOGLE_VERTEX_LOCATION"]
    private val responseType = ":generateContent"

    override val url = "$baseUrl/$projectId/locations/$location/publishers/google/models/$model:$responseType"

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
            logger.error("Unable to retrieve access token", e)
            exitProcess(1)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.simpleName)
    }
}
