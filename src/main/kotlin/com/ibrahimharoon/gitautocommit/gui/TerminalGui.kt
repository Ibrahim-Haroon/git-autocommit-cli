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

/**
 * Handles terminal-based user interactions for reviewing and editing generated messages.
 *
 * This class provides a text-based user interface for displaying generated commit messages
 * or PR summaries, and allows users to accept, edit, or regenerate these messages.
 *
 * @property messageTitle The title to be displayed before the generated message, (i.e. Commit or PR)
 * @property promptMessage The message to prompt the user for action.
 * @property options The [SummaryOptions] containing configuration for the summarization process.
 * @param initialMessage The initially generated message to be displayed.
 */
class TerminalGui(
    private val messageTitle: String,
    private val promptMessage: String,
    private val options: SummaryOptions,
    initialMessage: String
) {
    private val terminal = Terminal()
    private var message: String = initialMessage

    /**
     * Initiates the user interaction process.
     *
     * This method displays the generated message and prompts the user for actions such as
     * accepting, editing, or regenerating the message.
     *
     * @return The final message after user interaction, or an empty string if cancelled.
     */
    fun interactWithUser(): String {
        while (true) {
            displayMessage()

            val prompt = StringPrompt(
                prompt = TextColors.cyan(promptMessage),
                terminal = terminal,
                default = "",
                choices = listOf("y", "n", "edit", "regen")
            )

            when (prompt.ask()?.lowercase()?.trim()) {
                "n" -> return handleCancellation()
                "edit" -> handleEdit()
                "regen" -> handleRegen()
                else -> return handleConfirmation()  // by default empty or 'y' means confirmed
            }
        }
    }

    /**
     * Displays the current message in the terminal.
     */
    private fun displayMessage() {
        terminal.cursor.move {
            up(terminal.info.height)
            startOfLine()
            clearScreenAfterCursor()
        }

        val messagePanel = Panel(
            content = TextStyles.bold(TextColors.white(messageTitle)) +
                TextStyles.bold(TextColors.yellow("\n$message")),
            borderStyle = TextColors.rgb("#4b25b9"),
            borderType = BorderType.SQUARE_DOUBLE_SECTION_SEPARATOR,
            expand = true
        )

        terminal.println(messagePanel)
    }

    /**
     * Handles the user's confirmation of the current message.
     *
     * @return The confirmed message.
     */
    private fun handleConfirmation(): String {
        terminal.println(TextColors.green("Successfully confirmed"))
        copyToClipboard(message)
        return message
    }

    /**
     * Handles the user's cancellation of the operation.
     *
     * @return An empty string to indicate cancellation.
     */
    private fun handleCancellation(): String {
        terminal.println(TextColors.yellow("Operation cancelled"))
        return ""
    }

    /**
     * Handles the user's request to edit the current message.
     */
    private fun handleEdit() {
        terminal.println(TextStyles.bold(TextColors.yellow("Copied message to clipboard! Paste to edit")))
        val editPrompt = StringPrompt(
            prompt = TextColors.cyan("Edit message"),
            terminal = terminal,
        )
        copyToClipboard(message.replace("\n", " ").trimIndent())
        message = editPrompt.ask() ?: message
    }

    /**
     * Handles the user's request to regenerate the message.
     */
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

    /**
     * Provides access to the underlying Terminal object.
     *
     * @return The Terminal object used for this GUI.
     */
    fun terminal() = terminal
}
