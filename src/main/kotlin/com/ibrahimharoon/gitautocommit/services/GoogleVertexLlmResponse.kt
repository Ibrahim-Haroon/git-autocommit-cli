package com.ibrahimharoon.gitautocommit.services

import com.ibrahimharoon.gitautocommit.rest.dtos.GoogleVertexResponseDto
import com.ibrahimharoon.gitautocommit.services.LlmConstants.Companion.COMMIT_PROMPT
import com.ibrahimharoon.gitautocommit.services.LlmConstants.Companion.PR_SUMMARY_PROMPT
import com.ibrahimharoon.gitautocommit.services.LlmConstants.Companion.ROLE
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.io.BufferedReader
import java.io.InputStreamReader

object GoogleVertexLlmResponse: LlmResponseService {
    private val restTemplate = RestTemplate()
    private const val PROJECT_ID = "l-ulti-tf-48hours-d55d"
    private const val LOCATION = "us-central1"
    private const val MODEL = "gemini-1.5-flash"
    private const val BASE_URL = "https://us-central1-aiplatform.googleapis.com/v1/projects"
    private const val URL = "$BASE_URL/$PROJECT_ID/locations/$LOCATION/publishers/google/models/$MODEL:generateContent"
    private val headers = HttpHeaders().apply {
        set("Content-Type", "application/json")
        set ("Authorization", "Bearer ${getAuthToken()}")
    }

    override fun getMessage(gitData: String, isPr: Boolean, additionalLlmPrompt: String): String {
        var prompt = ""
        if (additionalLlmPrompt.isNotEmpty()) {
            prompt = "Your previous commit message was no good. This is an additional prompt to help you out: $additionalLlmPrompt\n\n"
        }
        prompt += if (isPr) PR_SUMMARY_PROMPT else COMMIT_PROMPT

        val payload = mapOf(
            "contents" to listOf(
                mapOf(
                    "role" to "user",
                    "parts" to listOf(
                        mapOf(
                            "text" to "$prompt + \n + $gitData"
                        )
                    )
                )
            ),
            "systemInstruction" to mapOf(
                "role" to "system",
                "parts" to listOf(
                    mapOf(
                        "text" to ROLE
                    )
                )
            )
        )

        val entity = HttpEntity(payload, headers)

        val response = restTemplate.exchange<GoogleVertexResponseDto>(
            URL,
            HttpMethod.POST,
            entity,
            Map::class.java
        )

        println(response.body)

        return response.body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw IllegalStateException("No content found in the response")
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