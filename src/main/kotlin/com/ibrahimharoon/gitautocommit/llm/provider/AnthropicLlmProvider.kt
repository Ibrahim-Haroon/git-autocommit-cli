package com.ibrahimharoon.gitautocommit.llm.provider

import com.ibrahimharoon.gitautocommit.llm.LlmType.ANTHROPIC
import com.ibrahimharoon.gitautocommit.llm.registry.LlmRegistry
import com.ibrahimharoon.gitautocommit.llm.response.LlmResponse
import com.ibrahimharoon.gitautocommit.llm.service.AnthropicLlmResponseService
import com.ibrahimharoon.gitautocommit.templates.LlmPromptContextualizer
import com.ibrahimharoon.gitautocommit.templates.LlmTemplates.Companion.ROLE
import io.github.cdimascio.dotenv.Dotenv
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders

@LlmRegistry(ANTHROPIC)
object AnthropicLlmResponse {
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

    private val llmResponse = AnthropicLlmResponseService(
        MODEL,
        URL,
        headers
    )

    override fun getMessage(gitData: String, isPr: Boolean): String {
        try {
            logger.debug("Using Anthropic model to generate commit message")
            val prompt = LlmPromptContextualizer.generate(gitData, isPr)
            return llmResponse.response(ROLE, prompt)
        } catch (e: Exception) {
            logger.error("Error generating commit message", e)
            return "Error generating Anthropic commit message - make sure your API key is valid/set"
        }
    }
}
