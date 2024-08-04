package com.ibrahimharoon.gitautocommit.llm.provider

import com.ibrahimharoon.gitautocommit.cli.CliConfigManager
import com.ibrahimharoon.gitautocommit.llm.service.AnthropicLlmResponseService
import com.ibrahimharoon.gitautocommit.llm.service.LlmResponseService
import org.springframework.http.HttpHeaders

class AnthropicLlmProvider : DefaultLlmProvider() {
    override val model = "claude-3-sonnet-20240229"
    override val url = "https://api.anthropic.com/v1/messages"

    private val anthropicApiKey = CliConfigManager["ANTHROPIC_API_KEY"]

    private val headers = HttpHeaders().apply {
        set("Content-Type", "application/json")
        set("x-api-key", anthropicApiKey)
        set("anthropic-version", "2023-06-01")
    }

    override val responseService: LlmResponseService = AnthropicLlmResponseService(model, url, headers)
}
