package com.ibrahimharoon.gitautocommit.cli

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.Terminal
import com.ibrahimharoon.gitautocommit.services.GitService
import com.ibrahimharoon.gitautocommit.services.GoogleVertexLlmResponse
import com.ibrahimharoon.gitautocommit.services.LocalLlmResponse
import com.ibrahimharoon.gitautocommit.services.OpenaiLlmResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GitOperations {
    private val logger: Logger = LoggerFactory.getLogger("GitOperations")

    private fun getLlmMessage(config: CommitConfig, gitData: String, additionalLlmPrompt: String): String {
        return when {
            config.useLocal -> LocalLlmResponse.getMessage(gitData, config.isPr, additionalLlmPrompt)
            config.useOpenai -> OpenaiLlmResponse.getMessage(gitData, config.isPr, additionalLlmPrompt)
            config.useGoogle -> GoogleVertexLlmResponse.getMessage(gitData, config.isPr, additionalLlmPrompt)
            else -> {
                logger.info("No LLM response service selected. Exiting.")
                ""
            }
        }
    }

    fun generateMessage(config: CommitConfig, withGui: Boolean = true, additionalLlmPrompt: String = ""): String {
        val gitData = if (config.isPr) GitService.getGitLog() else GitService.getGitDiff()

        if (gitData.isEmpty()) {
            logger.info("No changes detected. Make sure you do `git add .` before. Exiting.")
            return ""
        }

        logger.debug("Got git diff successfully: {}", gitData)

        return if (withGui) {
            ProgressBarGui.start {
                getLlmMessage(config, gitData, additionalLlmPrompt)
            }
        } else {
            getLlmMessage(config, gitData, additionalLlmPrompt)
        }
    }

    fun performCommit(config: CommitConfig) {
        if (config.isPlainPr) {
            val commitMessage = generateMessage(config, withGui = false)
            Terminal().println(commitMessage)
            return
        }

        var commitMessage = generateMessage(config)

        if (commitMessage.isEmpty()) {
            logger.info("Failed to generate a commit message. Exiting.")
            return
        }

        val terminalInteraction = TerminalGui(
            commitMessageTitle = if (config.isPr) "Generated PR summary:\n" else "Generated commit message:\n",
            promptMessage = if (config.isPr) "Use this PR summary?:" else "Use this commit message?:",
            initialCommitMessage = commitMessage,
            config = config
        )

        commitMessage = terminalInteraction.interactWithUser()

        if (commitMessage.isEmpty()) {
            logger.info("Commit cancelled by user. Exiting.")
            return
        }

        if (config.isPr) {
            terminalInteraction.terminal().println("PR summary generated successfully \nCopied to clipboard!")
            return
        }

        try {
            val processBuilder = ProcessBuilder("git", "commit", "-m", commitMessage)
            processBuilder.inheritIO()

            val process = processBuilder.start()
            val exitCode = process.waitFor()
            val errorStream = process.errorStream.bufferedReader().readText()

            if (exitCode != 0) {
                logger.error("Error executing git commit command: $errorStream")
            } else {
                terminalInteraction.terminal().println(TextStyles.bold(TextColors.yellow("Copied to clipboard!")))
                logger.debug("Commit successful")
            }
        } catch (e: Exception) {
            logger.error("Exception during git commit: ${e.message}")
        }
    }
}
