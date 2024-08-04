package com.ibrahimharoon.gitautocommit.cli

data class CommitConfig(
    val useLocal: Boolean,
    val useOpenai: Boolean,
    val useAnthropic: Boolean,
    val useGoogle: Boolean,
    val isPr: Boolean,
    val isPlainPr: Boolean
)
