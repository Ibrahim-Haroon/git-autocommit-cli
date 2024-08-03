package com.ibrahimharoon.gitautocommit.services

import io.github.cdimascio.dotenv.Dotenv
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders

object OpenaiLlmResponse: LlmResponseService {
    private const val MODEL = "gpt-3.5-turbo"
    private const val URL = "https://api.openai.com/v1/chat/completions"
    private val dotenv = Dotenv.configure()
        .directory(System.getProperty("user.home") + "/.local/bin")
        .filename("autocommit-config.env")
        .load()
    private val openaiApiKey = dotenv["OPENAI_API_KEY"] ?: ""
    private val logger = LoggerFactory.getLogger(OpenaiLlmResponse::class.java)
    private val headers = HttpHeaders().apply {
        set("Content-Type", "application/json")
        set ("Authorization", "Bearer $openaiApiKey")
    }
    private val baseLlmResponse = DefaultLlmResponseService(
        MODEL,
        URL,
        headers
    )

    override fun getMessage(gitData: String, isPr: Boolean, additionalLlmPrompt: String): String {
        try {
            logger.debug("Using OpenAI model to generate commit message")
            return baseLlmResponse.getMessage(gitData, isPr, additionalLlmPrompt)
        } catch (e: Exception) {
            logger.error("Error generating commit message", e)
            return "Error generating OpenAI commit message - make sure your API key is valid/set"
        }
    }
}
