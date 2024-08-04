package com.ibrahimharoon.gitautocommit.services

import io.github.cdimascio.dotenv.Dotenv
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders

object AnthropicLlmResponse : LlmResponseService {
    private const val MODEL = "claude-3-sonnet-20240229"
    private const val URL = "https://api.anthropic.com/v1/messages"
    private val dotenv = Dotenv.configure()
        .directory(System.getProperty("user.home") + "/.local/bin")
        .filename("autocommit-config.env")
        .load()
    private val anthropicApiKey = dotenv["ANTHROPIC_API_KEY"] ?: ""
    private val logger = LoggerFactory.getLogger(AnthropicLlmResponse::class.java)
    private val headers = HttpHeaders().apply {
        set("Content-Type", "application/json")
        set("x-api-key", anthropicApiKey)
        set("anthropic-version", "2023-06-01")
    }

    private val baseLlmResponse = AnthropicLlmResponseService(
        MODEL,
        URL,
        headers
    )

    override fun getMessage(gitData: String, isPr: Boolean, additionalLlmPrompt: String): String {
        try {
            logger.debug("Using Anthropic model to generate commit message")
            return baseLlmResponse.getMessage(gitData, isPr, additionalLlmPrompt)
        } catch (e: Exception) {
            logger.error("Error generating commit message", e)
            return "Error generating Anthropic commit message - make sure your API key is valid/set"
        }
    }
}

fun main() {
    val res = AnthropicLlmResponse.getMessage("test", false, "test")

    println(res)
}
