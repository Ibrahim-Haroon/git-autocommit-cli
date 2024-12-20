package com.ibrahimharoon.gitautocommit.cli

import java.awt.EventQueue
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * This object is used for interacting and executing commands in the Terminal
 */
object TerminalService {
    private val lock = ReentrantLock()
    /**
     * Copies the provided text to the system clipboard.
     *
     * @param text The text to be copied to the clipboard.
     */
    fun copyToClipboard(text: String) = lock.withLock {
        EventQueue.invokeAndWait {
            val selection = StringSelection(text)
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(selection, null)
        }
    }

    /**
     * Executes a Git command and returns its output.
     *
     * @param command A list of strings representing the command to be executed.
     * @return The output of the command as a string, or empty string if the command produced no output.
     */
    fun executeCommand(command: List<String>): String = lock.withLock {
        val process = ProcessBuilder(command)
            .redirectErrorStream(true)
            .start()

        try {
            val stdout = BufferedReader(InputStreamReader(process.inputStream)).readText()
            if (!process.waitFor(2, TimeUnit.SECONDS)) {
                throw TimeoutException("Command execution timed out")
            }
            return stdout.trim().takeIf { it.isNotEmpty() } ?: ""
        } finally {
            process.destroy()
        }
    }
}
