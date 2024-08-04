package com.ibrahimharoon.gitautocommit.rest.dtos

/**
 * Represents the response structure from the Anthropic LLM API.
 *
 * @property content A list of Content objects representing the response content.
 */
data class AnthropicLlmResponseDto(
    val content: List<Content>
) {
    /**
     * Represents a single piece of content in the Anthropic LLM response.
     *
     * @property text The actual text content of the response.
     * @property type The type of the content (e.g., "text").
     */
    data class Content(
        val text: String,
        val type: String
    )
}
