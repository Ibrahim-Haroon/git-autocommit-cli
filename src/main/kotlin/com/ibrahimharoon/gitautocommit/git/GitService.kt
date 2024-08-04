package com.ibrahimharoon.gitautocommit.git

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Object responsible for interacting with Git and retrieving relevant information.
 *
 * This service encapsulates all direct interactions with Git, providing methods to retrieve
 * git diffs, logs, and other relevant information needed for generating commit messages or PR summaries.
 */
object GitService {

    /**
     * Retrieves the set of files to be excluded from git operations.
     *
     * This method reads the .autocommitIgnore file, if it exists, and returns a set of file patterns to be ignored.
     *
     * @return A HashSet of strings representing file patterns to be ignored.
     */
    private fun getFilesToExclude(): HashSet<String> {
        val autocommitIgnoreFile = File(".autocommitIgnore")
        return if (autocommitIgnoreFile.exists()) {
            autocommitIgnoreFile.readLines().filter { it.isNotBlank() }.toHashSet()
        } else {
            hashSetOf() // empty set
        }
    }

    private val excludedFiles = getFilesToExclude()

    /**
     * Retrieves the name of the current Git branch.
     *
     * @return A string representing the name of the current Git branch.
     */
    private fun getCurrentBranchName(): String {
        val gitBranchArgs = listOf("rev-parse", "--abbrev-ref", "HEAD")
        val cliCommand = buildList {
            add("git")
            addAll(gitBranchArgs)
        }
        return executeCommand(cliCommand) ?: ""
    }

    /**
     * Retrieves the Git log for the current branch.
     *
     * This method returns the commit messages for all commits between the current branch and the main branch.
     *
     * @return A string containing the Git log.
     */
    fun getGitLog(): String {
        val currBranch = getCurrentBranchName()
        val gitLogArgs = listOf("log", "main..$currBranch", "--pretty=format:%s")
        val cliCommand = buildList {
            add("git")
            addAll(gitLogArgs)
        }
        return executeCommand(cliCommand) ?: ""
    }

    /**
     * Retrieves the Git diff for staged changes.
     *
     * This method returns the diff of staged changes, excluding any files specified in .autocommitIgnore.
     *
     * @return A string containing the Git diff, or an empty string if no changes are detected.
     */
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

    /**
     * Executes a Git command and returns its output.
     *
     * @param command A list of strings representing the command to be executed.
     * @return The output of the command as a string, or null if the command produced no output.
     */
    private fun executeCommand(command: List<String>): String? {
        val process = ProcessBuilder(command)
            .redirectErrorStream(true)
            .start()

        val stdout = BufferedReader(InputStreamReader(process.inputStream)).readText()
        process.waitFor()

        return stdout.trim().takeIf { it.isNotEmpty() }
    }
}
