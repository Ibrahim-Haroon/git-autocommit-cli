package com.ibrahimharoon.gitautocommit.llm.memory

import com.ibrahimharoon.gitautocommit.llm.model.LlmMessage
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

object ConversationMemory {
    private val lock = ReentrantReadWriteLock()
    private val store: MutableList<LlmMessage> = mutableListOf()

    val history: List<LlmMessage>
        get() = lock.read {
            store.toList()
        }

    fun add(message: LlmMessage) {
        lock.write {
            store.add(message)
        }
    }
}
