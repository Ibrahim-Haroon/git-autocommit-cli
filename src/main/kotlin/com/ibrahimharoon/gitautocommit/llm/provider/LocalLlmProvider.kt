package com.ibrahimharoon.gitautocommit.llm.provider

import com.ibrahimharoon.gitautocommit.llm.service.DefaultLlmResponseService
import com.ibrahimharoon.gitautocommit.llm.service.LlmResponseService
import org.springframework.http.HttpHeaders

class LocalLlmProvider : DefaultLlmProvider() {
    override val model = "llama-2-13b-chat.Q4_K_M.gguf"
    override val url = "http://localhost:8080/v1/chat/completions"

    private val headers = HttpHeaders().apply {
        set("Content-Type", "application/json")
        set("Authorization", "Bearer no-key")
    }

    override val responseService: LlmResponseService = DefaultLlmResponseService(model, url, headers)
}
