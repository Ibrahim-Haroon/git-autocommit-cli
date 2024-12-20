package com.ibrahimharoon.gitautocommit.llm.service

import com.ibrahimharoon.gitautocommit.rest.dtos.AnthropicLlmResponseDto
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

class AnthropicLlmResponseService(
    private val model: String,
    private val url: String,
    private val headers: HttpHeaders
) : LlmResponseService {
    override fun response(role: String, prompt: String): String {
        val messages = listOf(
            mapOf(
                "role" to "user",
                "content" to prompt
            )
        )

        val payload = mapOf(
            "model" to model,
            "messages" to messages,
            "system" to role,
            "max_tokens" to 500
        )

        val entity = HttpEntity(payload, headers)
        val response = restTemplate.exchange<AnthropicLlmResponseDto>(
            url,
            HttpMethod.POST,
            entity,
            Map::class.java
        )

        return response.body?.content?.firstOrNull()?.text
            ?: throw IllegalStateException("No content found in the response")
    }

    companion object {
        private val restTemplate = RestTemplate()
    }
}
