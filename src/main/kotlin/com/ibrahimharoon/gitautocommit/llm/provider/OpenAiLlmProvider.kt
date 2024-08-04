package com.ibrahimharoon.gitautocommit.llm.provider

import com.ibrahimharoon.gitautocommit.llm.LlmType
import com.ibrahimharoon.gitautocommit.llm.registry.LlmRegistry
import com.ibrahimharoon.gitautocommit.llm.service.DefaultLlmResponseService
import com.ibrahimharoon.gitautocommit.llm.service.LlmResponseService
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.http.HttpHeaders

@LlmRegistry(LlmType.OPENAI)
class OpenAiLlmProvider : DefaultLlmProvider() {
    override val model = "gpt-3.5-turbo"
    override val url = "https://api.openai.com/v1/chat/completions"

    private val dotenv = Dotenv.configure()
        .directory(System.getProperty("user.home") + "/.local/bin")
        .filename("autocommit-config.env")
        .load()
    private val openaiApiKey = dotenv["OPENAI_API_KEY"] ?: ""

    private val headers = HttpHeaders().apply {
        set("Content-Type", "application/json")
        set("Authorization", "Bearer $openaiApiKey")
    }

    override val responseService: LlmResponseService = DefaultLlmResponseService(model, url, headers)
}
