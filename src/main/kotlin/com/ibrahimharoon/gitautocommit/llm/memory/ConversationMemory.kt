package com.ibrahimharoon.gitautocommit.llm.memory

import com.ibrahimharoon.gitautocommit.llm.model.LlmMessage
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write


/**
 * Thread-safe object responsible for managing conversation history between the user and LLM.
 *
 * This object provides synchronized access to the conversation history, ensuring thread safety
 * through the use of read-write locks. It maintains a list of messages exchanged during the
 * conversation and provides methods to access and modify this history.
 */
object ConversationMemory {
    private val lock = ReentrantReadWriteLock()
    private val store: MutableList<LlmMessage> = mutableListOf()

    /**
     * Provides access to the current conversation history.
     *
     * This property returns a defensive copy of the conversation history, protecting
     * the internal state from modification.
     *
     * @return A List containing all [LlmMessage]s in the conversation history.
     */
    val history: List<LlmMessage>
        get() = lock.read {
            store.toList()
        }

    /**
     * Adds a new message to the conversation history.
     *
     * @param message The [LlmMessage] to be added to the conversation history.
     */
    fun add(message: LlmMessage) {
        lock.write {
            store.add(message)
        }
    }
}
