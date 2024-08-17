package com.ibrahimharoon.gitautocommit.gui

import com.github.ajalt.mordant.animation.progress.advance
import com.github.ajalt.mordant.animation.progress.animateOnThread
import com.github.ajalt.mordant.animation.progress.execute
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.widgets.progress.marquee
import com.github.ajalt.mordant.widgets.progress.percentage
import com.github.ajalt.mordant.widgets.progress.progressBar
import com.github.ajalt.mordant.widgets.progress.progressBarLayout
import com.github.ajalt.mordant.widgets.progress.speed
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

/**
 * Provides a visual indication of ongoing background activity.
 *
 * This object creates and manages a progress bar in the terminal to inform the user
 * that an operation is in progress, particularly during potentially time-consuming
 * background tasks such as LLM API calls. It doesn't represent actual progress,
 * but rather serves as a "busy" indicator.
 */
object ProgressBarGui {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java.simpleName)

    /**
     * Executes a given task while displaying an activity indicator.
     *
     * This method runs the provided task in a separate thread and displays an
     * indeterminate progress bar in the terminal. The progress bar doesn't
     * represent actual progress, but informs the user that the application
     * is actively working on something.
     *
     * @param T The return type of the task.
     * @param task A lambda function representing the background task to be executed.
     * @return The result of the executed task.
     */
    fun <T : Any> start(task: () -> T): T {
        val taskExecutorService = Executors.newSingleThreadExecutor()

        try {
            val terminal = Terminal()
            val progress = progressBarLayout {
                marquee(terminal.theme.warning("Generating message"), width = 15)
                percentage()
                progressBar()
                speed("B/s", style = terminal.theme.info)
            }.animateOnThread(terminal)
            val progressFuture = progress.execute()

            val taskFuture = taskExecutorService.submit(task)

            progress.update { total = TOTAL_TIME }
            var elapsed = 0L
            while (!taskFuture.isDone) {
                if (elapsed >= TIMEOUT) {
                    progress.advance(TOTAL_TIME - elapsed)
                    break
                }
                progress.advance(100)
                Thread.sleep(50)
                elapsed += 100
            }

            progressFuture.get()
            val result = taskFuture.get()

            return result
        } catch (e: Exception) {
            logger.error("Error while generating commit message", e)
            throw e
        } finally {
            taskExecutorService.shutdown()
        }
    }

    /**
     * The total animation time for the progress bar. (2 seconds)
     * This doesn't represent actual task duration, but controls the animation cycle.
     */
    private const val TOTAL_TIME = 2_000L

    /**
     * The timeout duration for the task. (2.1 seconds)
     * If the task exceeds this duration, it will be cancelled.
     */
    private const val TIMEOUT = 2_001L
}
