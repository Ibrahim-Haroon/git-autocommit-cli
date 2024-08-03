package com.ibrahimharoon.gitautocommit.services

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders

object LocalLlmResponse : LlmResponseService {
    private const val MODEL = "llama-2-13b-chat.Q4_K_M.gguf"
    private const val URL = "http://localhost:8080/v1/chat/completions"
    private val logger = LoggerFactory.getLogger(OpenaiLlmResponse::class.java)
    private val headers = HttpHeaders().apply {
        set("Content-Type", "application/json")
        set("Authorization", "Bearer no-key")
    }
    private val baseLlmResponse = DefaultLlmResponseService(
        MODEL,
        URL,
        headers
    )

    override fun getMessage(gitData: String, isPr: Boolean, additionalLlmPrompt: String): String {
        try {
            logger.debug("Using local model to generate commit message")
            return baseLlmResponse.getMessage(gitData, isPr, additionalLlmPrompt)
        } catch (e: Exception) {
            logger.error("Error generating commit message", e)
            return "Error generating Local commit message"
        }
    }
}
