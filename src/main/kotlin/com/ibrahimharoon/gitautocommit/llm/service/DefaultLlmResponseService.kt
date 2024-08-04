package com.ibrahimharoon.gitautocommit.services

import com.ibrahimharoon.gitautocommit.rest.dtos.DefaultLlmResponseDto
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

class DefaultLlmResponseService(
    private val model: String,
    private val url: String,
    private val headers: HttpHeaders
) : LlmResponseService {
    override fun response(role: String, prompt: String): String {
        val payload = mapOf(
            "model" to model,
            "messages" to listOf(
                mapOf(
                    "role" to "system",
                    "content" to role
                ),
                mapOf(
                    "role" to "user",
                    "content" to prompt
                )
            )
        )

        val entity = HttpEntity(payload, headers)
        val response = restTemplate.exchange<DefaultLlmResponseDto>(
            url,
            HttpMethod.POST,
            entity,
            Map::class.java
        )

        return response.body?.choices?.firstOrNull()?.message?.content
            ?: throw IllegalStateException("No content found in the response")
    }

    companion object {
        private val restTemplate = RestTemplate()
    }
}
