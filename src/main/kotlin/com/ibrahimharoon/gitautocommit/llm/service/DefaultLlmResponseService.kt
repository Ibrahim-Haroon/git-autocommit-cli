package com.ibrahimharoon.gitautocommit.llm.service

import com.ibrahimharoon.gitautocommit.llm.model.LlmMessage
import com.ibrahimharoon.gitautocommit.rest.dtos.DefaultLlmResponseDto
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class DefaultLlmResponseService(
    private val model: String,
    private val url: String,
    private val headers: HttpHeaders
) : LlmResponseService {
    private val lock = ReentrantLock()

    override fun response(role: String, prompt: String, conversationHistory: List<LlmMessage>?): String = lock.withLock {
        val prevConversations: List<Map<String, String>> = conversationHistory?.map { message ->
            mapOf(
                "role" to message.role.value,
                "content" to message.content
            )
        } ?: emptyList()

        val payload = mapOf(
            "model" to model,
            "messages" to listOf(
                mapOf(
                    "role" to "system",
                    "content" to role
                )
            ) + prevConversations + listOf(
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
