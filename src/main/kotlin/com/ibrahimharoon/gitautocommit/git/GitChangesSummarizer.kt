package com.ibrahimharoon.gitautocommit.git

import com.github.ajalt.mordant.rendering.TextColors
import com.ibrahimharoon.gitautocommit.cli.TerminalService
import com.ibrahimharoon.gitautocommit.core.SummaryOptions
import com.ibrahimharoon.gitautocommit.gui.ProgressBarGui
import com.ibrahimharoon.gitautocommit.gui.TerminalGui
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess

/**
 * Object responsible for summarizing git changes, generating commit messages or PR summaries.
 *
 * This object handles the core functionality of generating, reviewing, and applying summaries
 * for git changes, whether they are commit messages or PR summaries. It interacts with the LLM provider,
 * user interface components, and git commands to complete the summarization process.
 */
object GitChangesSummarizer {
    private val logger = LoggerFactory.getLogger(this::class.java.simpleName)

    /**
     * Summarizes git changes based on the provided options.
     *
     * This method orchestrates the entire process of generating a summary, including user interaction
     * if required, and applying the summary (either as a commit message or PR summary).
     *
     * @param options The [SummaryOptions] containing configuration for the summarization process.
     */
    fun summarizeChanges(options: SummaryOptions) {
        if (!options.withGui) {
            val message = generateMessage(options, withGui = false)

            if (System.getenv().containsKey("IS_WORKFLOW")) {
                File("pr_summary.txt").writeText(message)
                return
            }

            println(message)
            return
        }

        var message = generateMessage(options)

        if (message.isEmpty()) {
            logger.info("Failed to generate a message. Exiting.")
            return
        }

        val terminalInteraction = TerminalGui(
            messageTitle = if (options.isPr) "Generated PR summary:" else "Generated commit message:",
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

    /**
     * Generates a message summarizing git changes.
     *
     * This method retrieves git data (either diff or log), and uses the LLM provider to generate
     * a summary. It can optionally display a progress bar during the generation process.
     *
     * @param options The [SummaryOptions] containing configuration for the summarization process.
     * @param withGui Whether to display a progress bar during message generation.
     * @return The generated summary message as a string.
     */
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

    /**
     * Handles the process of finalizing a PR summary.
     *
     * This method copies the PR summary to the clipboard and notifies the user.
     *
     * @param message The PR summary message.
     * @param terminalGui The [TerminalGui] instance for user interaction.
     */
    private fun handlePrMessage(message: String, terminalGui: TerminalGui) {
        TerminalService.copyToClipboard(message)
        terminalGui.terminal().println("PR summary generated successfully")
        terminalGui.terminal().println(TextColors.yellow("PR summary copied to clipboard!"))
    }

    /**
     * Handles the process of creating a git commit with the generated message.
     *
     * This method executes the git commit command with the provided message, handles any errors,
     * and notifies the user of the result.
     *
     * @param message The commit message.
     * @param terminalGui The [TerminalGui] instance for user interaction.
     */
    private fun handleCommitMessage(message: String, terminalGui: TerminalGui) {
        try {
            val processBuilder = ProcessBuilder("git", "commit", "-m", message)
            processBuilder.inheritIO()

            val process = processBuilder.start()
            val exitCode = process.waitFor()

            if (exitCode != 0) {
                val errorStream = process.errorStream.bufferedReader().readText()
                logger.error("Error executing git commit command: $errorStream")
                exitProcess(1)
            } else {
                terminalGui.terminal().println(TextColors.yellow("Commit successful!"))
                logger.debug("Commit successful")
            }
        } catch (e: Exception) {
            logger.error("Exception during git commit: ${e.message}")
            exitProcess(1)
        }
    }
}
