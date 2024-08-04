package com.ibrahimharoon.gitautocommit.llm.provider

import com.ibrahimharoon.gitautocommit.llm.LlmType.LOCAL
import com.ibrahimharoon.gitautocommit.llm.registry.LlmRegistry
import com.ibrahimharoon.gitautocommit.llm.response.LlmResponse
import com.ibrahimharoon.gitautocommit.llm.service.DefaultLlmResponseService
import com.ibrahimharoon.gitautocommit.templates.LlmPromptContextualizer
import com.ibrahimharoon.gitautocommit.templates.LlmTemplates.Companion.ROLE
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders

@LlmRegistry(LOCAL)
object LocalLlmResponse: LlmResponse {
    private const val MODEL = "llama-2-13b-chat.Q4_K_M.gguf"
    private const val URL = "http://localhost:8080/v1/chat/completions"
    private val logger = LoggerFactory.getLogger(LocalLlmResponse::class.java)
    private val headers = HttpHeaders().apply {
        set("Content-Type", "application/json")
        set("Authorization", "Bearer no-key")
    }
    private val baseLlmResponse = DefaultLlmResponseService(
        MODEL,
        URL,
        headers
    )

    override fun getMessage(gitData: String, isPr: Boolean): String {
        try {
            logger.debug("Using local model to generate commit message")
            val prompt = LlmPromptContextualizer.generate(gitData, isPr)
            return baseLlmResponse.response(ROLE, prompt)
        } catch (e: Exception) {
            logger.error("Error generating commit message", e)
            return "Error generating Local commit message"
        }
    }
}
