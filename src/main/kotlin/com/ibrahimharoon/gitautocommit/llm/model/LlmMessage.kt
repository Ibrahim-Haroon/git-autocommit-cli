package com.ibrahimharoon.gitautocommit.llm.model

/**
 * Represents a message object, passed in the payload request to LLM for conversation history
 *
 * @property role Agnostic roles across providers
 * @property content Response from either the user or assistant role
 */
data class LlmMessage(
    val role: Role,
    val content: String
) {
    enum class Role(val value: String) {
        User("user"),
        Assistant("assistant");

        companion object {
            fun fromString(value: String): Role? {
                return entries.find { it.value == value }
            }
        }
    }
}

