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

    private val PROJECT_ID = CliConfigManager["GOOGLE_VERTEX_PROJECT_ID"]
    private val LOCATION = CliConfigManager["GOOGLE_VERTEX_LOCATION"]

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
            logger.error("Unable to retrieve access token", e)
            exitProcess(1)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.simpleName)
    }
}
