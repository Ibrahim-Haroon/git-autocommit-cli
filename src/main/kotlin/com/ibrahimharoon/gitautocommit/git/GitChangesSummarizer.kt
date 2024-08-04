package com.ibrahimharoon.gitautocommit.git

import com.github.ajalt.mordant.rendering.TextColors
import com.ibrahimharoon.gitautocommit.core.SummaryOptions
import com.ibrahimharoon.gitautocommit.gui.ProgressBarGui
import com.ibrahimharoon.gitautocommit.gui.TerminalGui
import org.slf4j.LoggerFactory
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

object GitChangesSummarizer {
    private val logger = LoggerFactory.getLogger(GitChangesSummarizer::class.java)

    fun summarizeChanges(options: SummaryOptions) {
        if (options.isPlainPr) {
            val message = generateMessage(options, withGui = false)
            println(message)
            return
        }

        var message = generateMessage(options)

        if (message.isEmpty()) {
            logger.info("Failed to generate a message. Exiting.")
            return
        }

        val terminalInteraction = TerminalGui(
            messageTitlePrefix = if (options.isPr) "Generated PR summary:" else "Generated commit message:",
            promptMessage = if (options.isPr) "Use this PR summary?" else "Use this commit message?",
            initialMessage = message,
            options = options
        )

        message = terminalInteraction.interactWithUser()

        if (message.isEmpty()) {
            logger.info("Operation cancelled by user. Exiting.")
            return
        }

        if (options.isPr) {
            handlePrMessage(message, terminalInteraction)
        } else {
            handleCommitMessage(message, terminalInteraction)
        }
    }

    fun generateMessage(options: SummaryOptions, withGui: Boolean = true): String {
        val gitData = if (options.isPr) GitService.getGitLog() else GitService.getGitDiff()

        if (gitData.isEmpty()) {
            logger.info("No changes detected. Make sure you have staged changes or commits. Exiting.")
            return ""
        }

        logger.debug("Got git data successfully: {}", gitData)

        return if (withGui) {
            ProgressBarGui.start {
                options.llmProvider.getMessage(gitData, options.isPr)
            }
        } else {
            options.llmProvider.getMessage(gitData, options.isPr)
        }
    }

    private fun handlePrMessage(message: String, terminalInteraction: TerminalGui) {
        copyToClipboard(message)
        terminalInteraction.terminal().println("PR summary generated successfully")
        terminalInteraction.terminal().println(TextColors.yellow("Copied to clipboard!"))
    }

    private fun handleCommitMessage(message: String, terminalInteraction: TerminalGui) {
        try {
            val processBuilder = ProcessBuilder("git", "commit", "-m", message)
            processBuilder.inheritIO()

            val process = processBuilder.start()
            val exitCode = process.waitFor()

            if (exitCode != 0) {
                val errorStream = process.errorStream.bufferedReader().readText()
                logger.error("Error executing git commit command: $errorStream")
            } else {
                copyToClipboard(message)
                terminalInteraction.terminal().println("Commit successful. Message copied to clipboard!")
                logger.debug("Commit successful")
            }
        } catch (e: Exception) {
            logger.error("Exception during git commit: ${e.message}")
        }
    }

    private fun copyToClipboard(text: String) {
        val selection = StringSelection(text)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(selection, null)
    }
}
