package com.ibrahimharoon.gitautocommit.cache

object RegenConversationCache : LocalCache {
    private val cache: HashMap<String, String> = HashMap()
    private var counter: Int = 0

    override fun set(key: String, value: String) {
        cache["$key-$counter"] = value
        counter++
    }

    override fun isNotEmpty(): Boolean {
        return cache.isNotEmpty()
    }

    override fun toString(): String {
        return cache.toString()
    }
}
