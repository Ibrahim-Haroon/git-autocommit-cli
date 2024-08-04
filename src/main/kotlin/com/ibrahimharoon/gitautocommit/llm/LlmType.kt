package com.ibrahimharoon.gitautocommit.llm

/**
 * Enum representing the different types of Language Model (LLM) providers supported by the system.
 *
 * This enum is used to identify and select the appropriate LLM provider throughout the application.
 * Each enum constant corresponds to a specific LLM service or implementation.
 */
enum class LlmType {
    /**
     * Represents a local LLM implementation.
     * This could be used for offline processing or when using a self-hosted model.
     */
    LOCAL,

    /**
     * Represents the OpenAI LLM provider.
     * This is used when interacting with OpenAI's API services, such as GPT-3 or GPT-4.
     */
    OPENAI,

    /**
     * Represents the Anthropic LLM provider.
     * This is used when interacting with Anthropic's API services, such as their Claude model.
     */
    ANTHROPIC,

    /**
     * Represents the Google Vertex AI LLM provider.
     * This is used when interacting with Google's Vertex AI platform for machine learning models.
     */
    GOOGLE_VERTEX
}
