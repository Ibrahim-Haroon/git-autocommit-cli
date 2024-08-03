package com.ibrahimharoon.gitautocommit.cache

object RegenConversationCache: LocalCache<String, String> {
    private val cache: HashMap<String, String> = HashMap()
    private var counter: Int = 0

    override fun set(key: String, value: String) {
        cache["$key-$counter"] = value
        counter++
    }

    fun isNotEmpty(): Boolean {
        return cache.isNotEmpty()
    }

    fun clear() {
        cache.clear()
    }

    override fun toString(): String {
        return cache.toString()
    }
}