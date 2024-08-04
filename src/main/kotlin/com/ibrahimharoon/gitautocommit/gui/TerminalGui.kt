package com.ibrahimharoon.gitautocommit.gui

import com.github.ajalt.mordant.rendering.BorderType
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.StringPrompt
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.widgets.Panel
import com.ibrahimharoon.gitautocommit.core.SummaryOptions
import com.ibrahimharoon.gitautocommit.git.GitChangesSummarizer
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class TerminalGui(
    private val messageTitlePrefix: String,
    private val promptMessage: String,
    private val options: SummaryOptions,
    initialMessage: String
) {
    private val terminal = Terminal()
    private var message: String = initialMessage

    fun interactWithUser(): String {
        while (true) {
            displayMessage()

            val prompt = StringPrompt(
                prompt = TextColors.cyan(promptMessage),
                terminal = terminal,
                choices = listOf("y", "n", "edit", "regen")
            )

            when (prompt.ask()?.lowercase()?.trim()) {
                "y", "" -> return handleConfirmation()
                "n" -> return handleCancellation()
                "edit" -> handleEdit()
                "regen" -> handleRegen()
                else -> terminal.println(TextColors.red("Invalid input. Please try again."))
            }
        }
    }

    private fun displayMessage() {
        terminal.cursor.move {
            up(terminal.info.height)
            startOfLine()
            clearScreenAfterCursor()
        }

        val messagePanel = Panel(
            content = TextStyles.bold(TextColors.white(messageTitlePrefix)) +
                TextStyles.bold(TextColors.yellow("\n$message")),
            borderStyle = TextColors.rgb("#4b25b9"),
            borderType = BorderType.SQUARE_DOUBLE_SECTION_SEPARATOR,
            expand = true
        )

        terminal.println(messagePanel)
    }

    private fun handleConfirmation(): String {
        terminal.println(TextColors.green("Successfully confirmed"))
        copyToClipboard(message)
        return message
    }

    private fun handleCancellation(): String {
        terminal.println(TextColors.yellow("Operation cancelled"))
        return ""
    }

    private fun handleEdit() {
        terminal.println(TextStyles.bold(TextColors.yellow("Copied message to clipboard! Paste to edit")))
        val editPrompt = StringPrompt(
            prompt = TextColors.cyan("Edit message"),
            terminal = terminal,
        )
        copyToClipboard(message.replace("\n", " ").trimIndent())
        message = editPrompt.ask() ?: message
    }

    private fun handleRegen() {
        terminal.println(TextColors.yellow("Regenerating message..."))

        val additionalPrompt = StringPrompt(
            prompt = TextColors.cyan("Pass additional prompt to LLM (optional)"),
            terminal = terminal,
        ).ask()

        if (additionalPrompt.isNullOrBlank()) {
            terminal.println(TextStyles.bold(TextColors.yellow("No additional prompt provided. Using default settings.")))
        }

        message = GitChangesSummarizer.generateMessage(
            options,
            withGui = false,
        )

        if (message.isEmpty()) {
            terminal.println(TextStyles.bold(TextColors.red("Failed to generate a new message. Keeping the previous one.")))
        }
    }

    private fun copyToClipboard(text: String) {
        val stringSelection = StringSelection(text)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(stringSelection, null)
    }

    fun terminal() = terminal
}
