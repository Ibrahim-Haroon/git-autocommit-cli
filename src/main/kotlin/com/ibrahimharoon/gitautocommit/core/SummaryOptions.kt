package com.ibrahimharoon.gitautocommit.cli

import com.ibrahimharoon.gitautocommit.llm.provider.LlmProvider

data class SummaryOptions(
    val llmProvider: LlmProvider,
    val isPr: Boolean,
    val isPlainPr: Boolean
)