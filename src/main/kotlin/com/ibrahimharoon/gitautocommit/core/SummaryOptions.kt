package com.ibrahimharoon.gitautocommit.core

import com.ibrahimharoon.gitautocommit.llm.provider.LlmProvider

/**
 * Data class representing the options for generating a summary (commit message or PR summary).
 *
 * This class encapsulates the configuration needed to generate a summary using a Language Model (LLM).
 * It includes the selected LLM provider and flags to determine the type of summary to be generated.
 *
 * @property llmProvider The LLM provider to be used for generating the summary.
 * @property isPr Flag indicating whether the summary is for a Pull Request (true) or a commit message (false).
 * @property withGui Flag indicating whether to generate a PR summary with GUI interaction.
 */
data class SummaryOptions(
    val llmProvider: LlmProvider,
    val isPr: Boolean,
    val withGui: Boolean
)
