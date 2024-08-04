package com.ibrahimharoon.gitautocommit.llm.provider

import com.ibrahimharoon.gitautocommit.llm.LlmType
import com.ibrahimharoon.gitautocommit.llm.registry.LlmRegistry
import com.ibrahimharoon.gitautocommit.llm.service.AnthropicLlmResponseService
import com.ibrahimharoon.gitautocommit.llm.service.LlmResponseService
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.http.HttpHeaders

@LlmRegistry(LlmType.ANTHROPIC)
class AnthropicLlmProvider : DefaultLlmProvider() {
    override val model = "claude-3-sonnet-20240229"
    override val url = "https://api.anthropic.com/v1/messages"

    private val dotenv = Dotenv.configure()
        .directory(System.getProperty("user.home") + "/.local/bin")
        .filename("autocommit-config.env")
        .load()
    private val anthropicApiKey = dotenv["ANTHROPIC_API_KEY"] ?: ""

    private val headers = HttpHeaders().apply {
        set("Content-Type", "application/json")
        set("x-api-key", anthropicApiKey)
        set("anthropic-version", "2023-06-01")
    }

    override val responseService: LlmResponseService = AnthropicLlmResponseService(model, url, headers)
}