package com.ibrahimharoon.gitautocommit.llm.memory

import com.ibrahimharoon.gitautocommit.llm.model.LlmMessage

object ConversationMemory {
    private val store: MutableList<LlmMessage> = mutableListOf()

    val history: List<LlmMessage>
        get() = store.toList()

    fun add(message: LlmMessage) {
        store.add(message)
    }
}
