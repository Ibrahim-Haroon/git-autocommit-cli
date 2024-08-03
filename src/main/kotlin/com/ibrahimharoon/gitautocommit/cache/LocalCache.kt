package com.ibrahimharoon.gitautocommit.cache

interface LocalCache<K, V> {
    operator fun set(key: K, value: V)
}
