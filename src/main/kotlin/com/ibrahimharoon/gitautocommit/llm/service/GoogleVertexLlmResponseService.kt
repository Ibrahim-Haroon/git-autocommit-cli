package com.ibrahimharoon.gitautocommit.llm.service

import com.ibrahimharoon.gitautocommit.llm.model.LlmMessage
import com.ibrahimharoon.gitautocommit.rest.dtos.GoogleVertexResponseDto
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class GoogleVertexLlmResponseService(
    private val url: String,
    private val headers: HttpHeaders
) : LlmResponseService {
    private val lock = ReentrantLock()

    override fun response(role: String, prompt: String, conversationHistory: List<LlmMessage>?): String = lock.withLock {
        val prevConversations = conversationHistory?.map { message ->
            mapOf(
                "role" to when (message.role.value) {
                    "assistant" -> "model"
                    else -> "user"
                },
                "parts" to listOf(
                    mapOf(
                        "text" to message.content
                    )
                )
            )
        } ?: emptyList()

        val payload = mapOf(
            "contents" to prevConversations + listOf(
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
