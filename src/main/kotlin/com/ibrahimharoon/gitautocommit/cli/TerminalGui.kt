package com.ibrahimharoon.gitautocommit.cli

import com.github.ajalt.mordant.rendering.BorderType.Companion.SQUARE_DOUBLE_SECTION_SEPARATOR
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.StringPrompt
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.widgets.Panel
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class TerminalGui(
    private val commitMessageTitle: String,
    private val promptMessage: String,
    private val config: CommitConfig,
    initialCommitMessage: String
) {
    private val terminal = Terminal()
    private var commitMessage: String = initialCommitMessage

    fun terminal() = terminal

    fun interactWithUser(): String {
        while (true) {
            displayCommitMessage()

            val prompt = StringPrompt(
                prompt = TextColors.cyan(promptMessage),
                terminal = terminal,
                default = "",
                choices = listOf("y", "n", "edit", "regen")
            )

            val userInput = prompt.ask()

            when (userInput?.lowercase()?.trim()) {
                "y", "" -> return handleConfirmation()
                "n" -> return handleCancellation()
                "edit" -> handleEdit()
                "regen" -> {
                    handleRegen()
                    if (commitMessage.isEmpty()) {
                        return ""
                    }
                }
            }
        }
    }

    private fun displayCommitMessage() {
        terminal.cursor.move {
            up(terminal.info.height)
            startOfLine()
            clearScreenAfterCursor()
        }

        val commitMessagePanel = Panel(
            content = TextStyles.bold(TextColors.white(commitMessageTitle)) +
                    TextStyles.bold(TextColors.yellow(commitMessage)),
            borderStyle = TextColors.rgb("#4b25b9"),
            borderType = SQUARE_DOUBLE_SECTION_SEPARATOR,
            expand = true
        )

        terminal.println(commitMessagePanel)
    }

    private fun handleConfirmation(): String {
        terminal.println("Successfully confirmed")
        copyToClipboard(commitMessage)
        return commitMessage
    }

    private fun handleCancellation(): String {
        terminal.println("Successfully cancelled")
        return ""
    }

    private fun handleEdit() {
        terminal.println(TextStyles.bold(TextColors.yellow("Copied message to clipboard! Paste to edit")))
        val editPrompt = StringPrompt(
            prompt = TextColors.cyan("Edit message"),
            terminal = terminal,
        )
        copyToClipboard(commitMessage.replace("\n", " ").trimIndent())
        commitMessage = editPrompt.ask()?.ifEmpty { commitMessage } ?: commitMessage
    }

    private fun handleRegen() {
        terminal.println("Regenerating commit message...")
        val additionalLlmPrompt = StringPrompt(
            prompt = TextColors.cyan("Pass additional prompt to LLM (optional)"),
            terminal = terminal,
        ).ask()

        if (additionalLlmPrompt.isNullOrBlank()) {
            terminal.println(TextStyles.bold(TextColors.yellow("No additional prompt provided. Using default settings.")))
        }

        commitMessage = GitOperations.generateMessage(
            config,
            withGui = false,
            additionalLlmPrompt ?: ""
        )
        if (commitMessage.isEmpty()) {
            terminal.println(TextStyles.bold(TextColors.red("Failed to generate a new commit message. Exiting.")))
        }
    }

    private fun copyToClipboard(text: String) {
        val stringSelection = StringSelection(text)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(stringSelection, null)
    }
}
