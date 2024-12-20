package com.ibrahimharoon.gitautocommit.llm.service

import com.ibrahimharoon.gitautocommit.llm.model.LlmMessage

interface LlmResponseService {
    /**
     * This function is used to create a baseline behavior for all LLM Response Services
     *
     * @param role: The behavior/persona for the model to inherit
     * @param prompt: The task for the model to complete (usually contextualized through a RAG framework)
     * @param conversationHistory: All previous conversations between user and assistant for history context
     * @return: response from LLM
     */
    fun response(role: String, prompt: String, conversationHistory: List<LlmMessage>?): String
}
