package com.ibrahimharoon.gitautocommit

import com.ibrahimharoon.gitautocommit.cli.CliArgParser
import com.ibrahimharoon.gitautocommit.cli.CliConfig
import com.ibrahimharoon.gitautocommit.cli.CommitConfig
import com.ibrahimharoon.gitautocommit.cli.GitOperations
import io.github.cdimascio.dotenv.Dotenv
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class LlmChoice(val name: String, val isSelected: Boolean)

fun main(args: Array<String>) {
    val logger: Logger = LoggerFactory.getLogger("Main")
    val cliArgs = CliArgParser.parse(args)
    CliConfig.createConfigIfNotExists()

    if (cliArgs.isTest) {
        println("CLI tool installed correctly")
        return
    }

    if (cliArgs.setDefault != null) {
        CliConfig.setDefaultLlmService(cliArgs.setDefault)
        logger.info("Default LLM response service set to ${cliArgs.setDefault}")
        return
    }

    if (cliArgs.setOpenaiApiKey != null) {
        CliConfig.setOpenaiApiKey(cliArgs.setOpenaiApiKey)
        logger.info("OpenAI API key set to ${cliArgs.setOpenaiApiKey.take(10)}...")
        return
    }

    if (cliArgs.setAnthropicApiKey != null) {
        CliConfig.setAnthropicApiKey(cliArgs.setAnthropicApiKey)
        logger.info("Anthropic API key is to ${cliArgs.setAnthropicApiKey.take(10)}...")
        return
    }

    if (cliArgs.setGoogleVertexProjectId != null) {
        CliConfig.setGoogleVertexProjectId(cliArgs.setGoogleVertexProjectId)
        logger.info("Google vertex project id set to ${cliArgs.setGoogleVertexProjectId}")
        return
    }

    if (cliArgs.setGoogleVertexLocation != null) {
        CliConfig.setGoogleVertexLocation(cliArgs.setGoogleVertexLocation)
        logger.info("Google vertex location set to ${cliArgs.setGoogleVertexLocation}")
        return
    }

    val dotenv = Dotenv.configure()
        .directory(System.getProperty("user.home") + "/.local/bin")
        .filename("autocommit-config.env")
        .load()

    val defaultLlm = dotenv["DEFAULT_LLM"] ?: "google"
    val llmChoices = listOf(
        LlmChoice("local", cliArgs.useLocal),
        LlmChoice("openai", cliArgs.useOpenai),
        LlmChoice("anthropic", cliArgs.useAnthropic),
        LlmChoice("google", cliArgs.useGoogle)
    )

    val selectedLlm = llmChoices.find { it.isSelected }?.name ?: defaultLlm
    val useLocal = selectedLlm == "local"
    val useOpenai = selectedLlm == "openai"
    val useAnthropic = selectedLlm == "anthropic"
    val useGoogle = selectedLlm == "google"
    val isPlainPr = cliArgs.isPlainPr
    val isPr = cliArgs.isPr || isPlainPr

    val config = CommitConfig(
        useLocal,
        useOpenai,
        useAnthropic,
        useGoogle,
        isPr,
        isPlainPr
    )

    logger.debug("Starting git autocommit")
    GitOperations.performCommit(config)
}
