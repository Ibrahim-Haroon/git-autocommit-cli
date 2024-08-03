package com.ibrahimharoon.gitautocommit.services

interface LlmResponseService {
    fun getMessage(gitData: String, isPr: Boolean, additionalLlmPrompt: String): String
}