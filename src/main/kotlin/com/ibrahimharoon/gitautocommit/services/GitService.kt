package com.ibrahimharoon.gitautocommit.services

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object GitService {
    private fun getFilesToExclude(): HashSet<String> {
        val autocommitIgnoreFile = File(".autocommitIgnore")
        return if (autocommitIgnoreFile.exists()) {
            autocommitIgnoreFile.readLines().filter { it.isNotBlank() }.toHashSet()
        } else {
            hashSetOf() // empty set
        }
    }

    private val excludedFiles = getFilesToExclude()

    private fun getCurrentBranchName(): String {
        val gitBranchArgs = listOf("rev-parse", "--abbrev-ref", "HEAD")

        val cliCommand = buildList {
            add("git")
            addAll(gitBranchArgs)
        }

        return executeCommand(cliCommand) ?: ""
    }

    fun getGitLog(): String {
        val currBranch = getCurrentBranchName()
        val gitLogArgs = listOf("log", "main..$currBranch", "--pretty=format:%s")

        val cliCommand = buildList {
            add("git")
            addAll(gitLogArgs)
        }

        return executeCommand(cliCommand) ?: ""
    }

    fun getGitDiff(): String {
        val gitDiffArgs = listOf("diff", "--cached", "--diff-algorithm=minimal")

        val cliCommand = buildList {
            add("git")
            addAll(gitDiffArgs)
            add("--name-only")
        }

        val modifiedFiles = executeCommand(cliCommand)
        if (modifiedFiles.isNullOrEmpty()) {
            return ""
        }

        val filteredModifiedFiles = modifiedFiles.lines().filter { file ->
            excludedFiles.none { excludedFile -> file.contains(excludedFile) }
        }

        if (filteredModifiedFiles.isEmpty()) {
            return ""
        }

        val diffCommand = buildList {
            add("git")
            addAll(gitDiffArgs)
        }
        val diffResult = executeCommand(diffCommand)

        return mapOf(
            "files" to filteredModifiedFiles,
            "diff" to (diffResult ?: "")
        ).toString()
    }

    private fun executeCommand(command: List<String>): String? {
        val process = ProcessBuilder(command)
            .redirectErrorStream(true)
            .start()

        val stdout = BufferedReader(InputStreamReader(process.inputStream)).readText()
        process.waitFor()

        return stdout.trim().takeIf { it.isNotEmpty() }
    }
}

fun main() {
    try {
        val stagedDiff = GitService.getGitLog()
        println(stagedDiff)
    } catch (e: Exception) {
        println(e.message)
    }
}