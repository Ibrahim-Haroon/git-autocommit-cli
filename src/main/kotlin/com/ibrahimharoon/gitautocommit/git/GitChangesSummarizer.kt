package com.ibrahimharoon.gitautocommit.cli

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.ibrahimharoon.gitautocommit.services.GitService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GitChangesSummarizer {
    private val logger: Logger = LoggerFactory.getLogger("GitOperations")

    private fun generateMessage(config: SummaryOptions, withGui: Boolean = true): String {
        val gitData = if (config.isPr) GitService.getGitLog() else GitService.getGitDiff()

        if (gitData.isEmpty()) {
            logger.info("No changes detected. Make sure you do `git add .` before. Exiting.")
            return ""
        }

        logger.debug("Got git diff successfully: {}", gitData)

        return if (withGui) {
            ProgressBarGui.start {
                config.llmProvider.getMessage(gitData, config.isPr)
            }
        } else {
            config.llmProvider.getMessage(gitData, config.isPr)
        }
    }

    fun performCommit(config: SummaryOptions) {
        if (config.isPlainPr) {
            val commitMessage = generateMessage(config, withGui = false)
            println(commitMessage)
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
