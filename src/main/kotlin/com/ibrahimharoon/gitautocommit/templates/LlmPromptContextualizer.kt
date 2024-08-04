package com.ibrahimharoon.gitautocommit.templates

import com.ibrahimharoon.gitautocommit.cache.LocalCache
import com.ibrahimharoon.gitautocommit.cache.RegenConversationCache
import com.ibrahimharoon.gitautocommit.templates.LlmTemplates.Companion.commitPrompt
import com.ibrahimharoon.gitautocommit.templates.LlmTemplates.Companion.prSummaryPrompt

object LlmPromptContextualizer {
    private val conversationCache = RegenConversationCache

    fun generate(gitData: String, isPr: Boolean): String {
        var prompt = ""
        prompt += if (isPr) prSummaryPrompt(gitData) else commitPrompt(gitData)
        prompt += if (conversationCache.isNotEmpty()) "Previous history: $conversationCache" else ""

        return prompt
    }
}
