package com.ibrahimharoon.gitautocommit.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

data class CliArguments(
    val setDefault: String?,
    val setOpenaiApiKey: String?,
    val setGoogleVertexProjectId: String?,
    val setGoogleVertexLocation: String?,
    val useLocal: Boolean,
    val useOpenai: Boolean,
    val useGoogle: Boolean,
    val isPr: Boolean,
    val isPlainPr: Boolean,
    val isTest: Boolean
)

object CliArgParser {
    fun parse(args: Array<String>): CliArguments {
        val parser = ArgParser("autocommit")

        val setDefault by parser.option(
            ArgType.Choice(listOf("local", "openai", "google"), { it }),
            fullName = "set-default",
            shortName = "d",
            description = "Set the default LLM response service"
        )

        val setOpenaiApiKey by parser.option(
            ArgType.String,
            fullName = "set-openai-key",
            description = "Set OpenAI API key"
        )

        val setGoogleVertexProjectId by parser.option(
            ArgType.String,
            fullName = "set-google-vertex-project-id",
            shortName = "vertex-project-id",
            description = "Set the Google vertex project ID"
        )

        val setGoogleVertexLocation by parser.option(
            ArgType.String,
            fullName = "set-google-vertex-location",
            shortName = "vertex-location",
            description = "Set the Google vertex location"
        )

        val useLocal by parser.option(
            ArgType.Boolean,
            fullName = "local",
            shortName = "l",
            description = "Use Local LLM response service"
        ).default(false)

        val useOpenai by parser.option(
            ArgType.Boolean,
            fullName = "openai",
            shortName = "o",
            description = "Use OpenAI LLM response service"
        ).default(false)

        val useGoogle by parser.option(
            ArgType.Boolean,
            fullName = "google",
            shortName = "g",
            description = "Use Google LLM response service"
        ).default(false)

        val isPr by parser.option(
            ArgType.Boolean,
            fullName = "make-pr-summary",
            shortName = "pr",
            description = "Create a summary based off git log for PR message"
        ).default(false)

        val isPlainPr by parser.option(
            ArgType.Boolean,
            fullName = "plain-pr",
            shortName = "plain-pr",
            description = "Create a summary based off git log for PR message without GUI"
        ).default(false)

        val isTest by parser.option(
            ArgType.Boolean,
            fullName = "test",
            shortName = "t",
            description = "Test CLI tool was installed correctly"
        ).default(false)

        parser.parse(args)
        return CliArguments(
            setDefault,
            setOpenaiApiKey,
            setGoogleVertexProjectId,
            setGoogleVertexLocation,
            useLocal,
            useOpenai,
            useGoogle,
            isPr,
            isPlainPr,
            isTest
        )
    }
}
