package com.ibrahimharoon.gitautocommit.cache

interface LocalCache {
    operator fun set(key: String, value: String)
    fun isNotEmpty(): Boolean
}
