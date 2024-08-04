package com.ibrahimharoon.gitautocommit.llm.provider

import com.ibrahimharoon.gitautocommit.cli.CliConfigManager
import com.ibrahimharoon.gitautocommit.llm.service.DefaultLlmResponseService
import com.ibrahimharoon.gitautocommit.llm.service.LlmResponseService
import org.springframework.http.HttpHeaders

class OpenAiLlmProvider : DefaultLlmProvider() {
    override val model = "gpt-3.5-turbo"
    override val url = "https://api.openai.com/v1/chat/completions"

    private val openaiApiKey = CliConfigManager["OPENAI_API_KEY"]

    private val headers = HttpHeaders().apply {
        set("Content-Type", "application/json")
        set("Authorization", "Bearer $openaiApiKey")
    }

    override val responseService: LlmResponseService = DefaultLlmResponseService(model, url, headers)
}
