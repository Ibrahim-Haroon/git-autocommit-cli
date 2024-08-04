package com.ibrahimharoon.gitautocommit.llm.provider

import com.ibrahimharoon.gitautocommit.llm.service.LlmResponseService

/**
 * Defines the contract for Large Language Model (LLM) providers in the git-autocommit system.
 *
 * This interface should be implemented by all classes that provide LLM functionality,
 * such as generating commit messages or PR summaries based on git data.
 */
interface LlmProvider {
    /**
     * The model identifier used by this LLM provider.
     *
     * This should be a string that uniquely identifies the model being used,
     * such as "gpt-3.5-turbo" for OpenAI or "claude-3-sonnet-20240229" for Anthropic.
     */
    val model: String

    /**
     * The URL endpoint for the LLM API.
     *
     * This should be the full URL to which API requests are sent.
     */
    val url: String

    /**
     * The service responsible for making API calls to the LLM provider.
     *
     * This property should be an instance of a class that implements the LlmResponseService interface,
     * which handles the actual HTTP requests to the LLM API.
     */
    val responseService: LlmResponseService

    /**
     * Generates a message based on the provided git data.
     *
     * This method should use the LLM to generate either a commit message or a PR summary,
     * depending on the [isPr] parameter.
     *
     * @param gitData A string containing the relevant git data (e.g., diff for commits, log for PRs).
     * @param isPr A boolean indicating whether to generate a PR summary (true) or a commit message (false).
     * @return A string containing the generated message.
     */
    fun getMessage(gitData: String, isPr: Boolean): String
}
