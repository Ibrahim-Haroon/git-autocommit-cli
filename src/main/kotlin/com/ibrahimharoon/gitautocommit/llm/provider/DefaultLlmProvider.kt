package com.ibrahimharoon.gitautocommit.llm.provider

import com.ibrahimharoon.gitautocommit.templates.LlmPromptContextualizer
import com.ibrahimharoon.gitautocommit.templates.LlmTemplates.Companion.ROLE
import org.slf4j.LoggerFactory

abstract class DefaultLlmProvider : LlmProvider {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun getMessage(gitData: String, isPr: Boolean): String {
        return try {
            logger.debug("Using ${this::class.simpleName} to generate commit message")
            val prompt = LlmPromptContextualizer.generate(gitData, isPr)
            responseService.response(ROLE, prompt)
        } catch (e: Exception) {
            logger.error("Error generating commit message", e)
            "Error generating ${this::class.simpleName} commit message - make sure your API key is valid/set"
        }
    }
}
