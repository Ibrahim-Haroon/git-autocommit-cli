package com.ibrahimharoon.gitautocommit.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

/**
 * Represents the parsed command-line arguments.
 *
 * This data class encapsulates all possible command-line options. When adding new
 * command-line arguments, extend this class with new properties.
 *
 * @property setDefault The default LLM response service to use.
 * @property setOpenaiApiKey The OpenAI API key.
 * @property setAnthropicApiKey The Anthropic API key.
 * @property setGoogleVertexProjectId The Google Vertex project ID.
 * @property setGoogleVertexLocation The Google Vertex location.
 * @property useLocal Flag to use the local LLM response service.
 * @property useOpenai Flag to use the OpenAI LLM response service.
 * @property useAnthropic Flag to use the Anthropic LLM response service.
 * @property useGoogle Flag to use the Google LLM response service.
 * @property isPr Flag to create a PR summary.
 * @property isPlainPr Flag to create a plain PR summary without GUI.
 * @property isTest Flag to test if the CLI tool was installed correctly.
 *
 * Note: Add new properties here as new command-line arguments are introduced.
 */
data class CliArguments(
    val setDefault: String?,
    val setOpenaiApiKey: String?,
    val setAnthropicApiKey: String?,
    val setGoogleVertexProjectId: String?,
    val setGoogleVertexLocation: String?,
    val useLocal: Boolean,
    val useOpenai: Boolean,
    val useAnthropic: Boolean,
    val useGoogle: Boolean,
    val isPr: Boolean,
    val isPlainPr: Boolean,
    val isTest: Boolean,
    val showVersion: Boolean
    // Add new properties for future arguments here
)

/**
 * Parses command-line arguments for the git-autocommit application.
 *
 * This object is responsible for defining, parsing, and organizing command-line arguments.
 * It uses the kotlinx.cli library to handle argument parsing and provides a structured
 * way to access parsed arguments.
 */
object CliArgParser {

    /**
     * Parses the command-line arguments.
     *
     * This method sets up the argument parser, defines all possible arguments,
     * parses the input, and returns a [CliArguments] object containing the parsed values.
     *
     * @param args The command-line arguments passed to the application.
     * @return A [CliArguments] object containing the parsed argument values.
     */
    fun parse(args: Array<String>): CliArguments {
        val parser = ArgParser("autocommit")

        val setDefault by parser.option(
            ArgType.Choice(
                listOf(
                    "local",
                    "openai",
                    "google",
                    "anthropic"
                ),
                { it }
            ),
            fullName = "set-default",
            shortName = "d",
            description = "Set the default LLM response service"
        )

        val setOpenaiApiKey by parser.option(
            ArgType.String,
            fullName = "set-openai-key",
            description = "Set OpenAI API key"
        )

        val setAnthropicApiKey by parser.option(
            ArgType.String,
            fullName = "set-anthropic-key",
            description = "Set the anthropic API key"
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

        val useAnthropic by parser.option(
            ArgType.Boolean,
            fullName = "anthropic",
            shortName = "a",
            description = "Use Anthropic LLM response service"
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

        val showVersion by parser.option(
            ArgType.Boolean,
            fullName = "version",
            shortName = "v",
            description = "Show the version of the tool"
        ).default(false)

        parser.parse(args)
        return CliArguments(
            setDefault,
            setOpenaiApiKey,
            setAnthropicApiKey,
            setGoogleVertexProjectId,
            setGoogleVertexLocation,
            useLocal,
            useOpenai,
            useAnthropic,
            useGoogle,
            isPr,
            isPlainPr,
            isTest,
            showVersion
        )
    }
}
