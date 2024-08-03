package com.ibrahimharoon.gitautocommit.services

import com.ibrahimharoon.gitautocommit.cache.RegenConversationCache
import com.ibrahimharoon.gitautocommit.rest.dtos.DefaultLlmResponseDto
import com.ibrahimharoon.gitautocommit.services.LlmConstants.Companion.COMMIT_PROMPT
import com.ibrahimharoon.gitautocommit.services.LlmConstants.Companion.PR_SUMMARY_PROMPT
import com.ibrahimharoon.gitautocommit.services.LlmConstants.Companion.ROLE
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
    private val conversationCache = RegenConversationCache

    override fun getMessage(gitData: String, isPr: Boolean, additionalLlmPrompt: String): String {
        var prompt = ""
        if (additionalLlmPrompt.isNotEmpty()) {
            prompt = "Your previous commit message was no good. This is an additional prompt to help you out: $additionalLlmPrompt\n\n"
        }
        prompt += if (isPr) PR_SUMMARY_PROMPT else COMMIT_PROMPT
        prompt += if (conversationCache.isNotEmpty()) "Previous history: $conversationCache" else ""

        val payload = mapOf(
            "model" to model,
            "messages" to listOf(
                mapOf(
                    "role" to "system",
                    "content" to ROLE
                ),
                mapOf(
                    "role" to "user",
                    "content" to "$prompt + \n + $gitData"
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
