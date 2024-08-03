package com.ibrahimharoon.gitautocommit.cli

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

object ProgressBarGui {
    private val logger: Logger = LoggerFactory.getLogger("ProgressBarGui")
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

            progress.update { total = totalTime }
            var elapsed = 0L
            while (!taskFuture.isDone) {
                progress.advance(100)
                Thread.sleep(50)
                elapsed += 100
            }

            if (elapsed >= timeout) {
                taskFuture.cancel(true)
                logger.warn("Task timed out after $timeout ms")
            }

            val result = taskFuture.get()
            progressFuture.get()

            return result
        } catch (e: Exception) {
            logger.error("Error while generating commit message", e)
            throw e
        } finally {
            taskExecutorService.shutdown()
        }
    }

    private const val totalTime = 2_000L
    private const val timeout = 2_0001L
}