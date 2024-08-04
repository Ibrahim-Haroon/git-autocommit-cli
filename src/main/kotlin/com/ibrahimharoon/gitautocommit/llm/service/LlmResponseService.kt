package com.ibrahimharoon.gitautocommit.llm.service

interface LlmResponseService {
    /**
     * This function is used to create a baseline behavior for all LLM Response Services
     *
     * @param role: The behavior/persona for the model to inherit
     * @param prompt: The task for the model to complete (usually contextualized through a RAG framework)
     * @return: response from LLM
     */
    fun response(role: String, prompt: String): String
}
