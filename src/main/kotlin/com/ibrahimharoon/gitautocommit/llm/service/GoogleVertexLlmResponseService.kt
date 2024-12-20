package com.ibrahimharoon.gitautocommit.llm.service

import com.ibrahimharoon.gitautocommit.rest.dtos.GoogleVertexResponseDto
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

class GoogleVertexLlmResponseService(
    private val url: String,
    private val headers: HttpHeaders
) : LlmResponseService {

    override fun response(role: String, prompt: String): String {
        val payload = mapOf(
            "contents" to listOf(
                mapOf(
                    "role" to "user",
                    "parts" to listOf(
                        mapOf(
                            "text" to prompt
                        )
                    )
                )
            ),
            "systemInstruction" to mapOf(
                "role" to "system",
                "parts" to listOf(
                    mapOf(
                        "text" to role
                    )
                )
            )
        )

        val entity = HttpEntity(payload, headers)

        val response = restTemplate.exchange<GoogleVertexResponseDto>(
            url,
            HttpMethod.POST,
            entity,
            Map::class.java
        )

        return response.body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw IllegalStateException("No content found in the response")
    }

    companion object {
        private val restTemplate = RestTemplate()
    }
}
