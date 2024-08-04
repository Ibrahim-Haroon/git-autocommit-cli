package com.ibrahimharoon.gitautocommit.rest.dtos

/**
 * Represents the default response structure from an LLM API.
 *
 * @property choices A list of Choice objects representing possible responses.
 */
data class DefaultLlmResponseDto(
    val choices: List<Choice>
) {
    /**
     * Represents a single choice in the LLM response.
     *
     * @property message The Message object containing the response content.
     */
    data class Choice(
        val message: Message
    ) {
        /**
         * Represents the message content of a choice.
         *
         * @property content The actual text content of the response.
         */
        data class Message(
            val content: String
        )
    }
}
