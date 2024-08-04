package com.ibrahimharoon.gitautocommit.llm

import com.ibrahimharoon.gitautocommit.cache.RegenConversationCache
import com.ibrahimharoon.gitautocommit.llm.templates.LlmTemplates.Companion.commitPrompt
import com.ibrahimharoon.gitautocommit.llm.templates.LlmTemplates.Companion.prSummaryPrompt

/**
 * Object responsible for contextualizing prompts for Language Model (LLM) interactions.
 *
 * This object generates appropriate prompts for LLMs based on the given git data and the type of
 * summary required (commit message or PR summary). It also incorporates conversation history
 * from the RegenConversationCache to provide context for regeneration requests.
 */
object LlmPromptContextualizer {

    /**
     * Cache for storing conversation history for regeneration requests.
     */
    private val conversationCache = RegenConversationCache

    /**
     * Generates a contextualized prompt for the LLM based on the provided git data and summary type.
     *
     * This method combines the appropriate template (commit or PR summary) with the git data
     * and any relevant conversation history to create a comprehensive prompt for the LLM.
     *
     * @param gitData A string containing the relevant git data (e.g., diff for commits, log for PRs).
     * @param isPr A boolean indicating whether to generate a PR summary (true) or a commit message (false).
     * @return A string containing the contextualized prompt for the LLM.
     */
    fun generate(gitData: String, isPr: Boolean): String {
        var prompt = ""
        prompt += if (isPr) prSummaryPrompt(gitData) else commitPrompt(gitData)
        prompt += if (conversationCache.isNotEmpty()) "Previous history: $conversationCache" else ""

        return prompt
    }
}
