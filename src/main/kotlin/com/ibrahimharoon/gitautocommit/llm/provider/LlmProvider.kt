package com.ibrahimharoon.gitautocommit.llm.provider

import com.ibrahimharoon.gitautocommit.llm.service.LlmResponseService

interface LlmProvider {
    val model: String
    val url: String
    val responseService: LlmResponseService

    fun getMessage(gitData: String, isPr: Boolean): String
}