package com.ibrahimharoon.gitautocommit.llm.provider

import com.ibrahimharoon.gitautocommit.llm.LlmPromptContextualizer
import com.ibrahimharoon.gitautocommit.llm.templates.LlmTemplates
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

/**
 * Provides a default implementation of the LlmProvider interface.
 *
 * This class serves as a base for specific LLM provider implementations,
 * handling common functionality such as logging and error handling.
 */
abstract class DefaultLlmProvider : LlmProvider {

    /**
     * Logger instance for this class.
     */
    private val logger = LoggerFactory.getLogger(this::class.java.simpleName)

    /**
     * Generates a message based on the provided git data.
     *
     * This implementation handles the common workflow of:
     * 1. Logging the attempt to generate a message.
     * 2. Contextualizing the prompt using LlmPromptContextualizer.
     * 3. Calling the LLM API through the responseService.
     * 4. Handling and logging any errors that occur during the process.
     *
     * @param gitData A string containing the relevant git data (e.g., diff for commits, log for PRs).
     * @param isPr A boolean indicating whether to generate a PR summary (true) or a commit message (false).
     * @return A string containing the generated message, or an error message if generation fails.
     */
    override fun getMessage(gitData: String, isPr: Boolean): String {
        return try {
            logger.debug("Using ${this::class.simpleName} to generate ${if (isPr) "PR summary" else "commit message"}")
            val prompt = LlmPromptContextualizer.generate(gitData, isPr)
            responseService.response(LlmTemplates.ROLE, prompt)
        } catch (e: Exception) {
            logger.error("Error generating ${this::class.simpleName} message - $e")
            exitProcess(1)
        }
    }
}
