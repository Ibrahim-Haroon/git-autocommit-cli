package com.ibrahimharoon.gitautocommit

import com.ibrahimharoon.gitautocommit.cli.CliArgParser
import com.ibrahimharoon.gitautocommit.cli.CliConfigManager
import com.ibrahimharoon.gitautocommit.core.SummaryOptions
import com.ibrahimharoon.gitautocommit.git.GitChangesSummarizer
import com.ibrahimharoon.gitautocommit.llm.LlmType
import com.ibrahimharoon.gitautocommit.llm.factory.LlmProviderFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    val logger: Logger = LoggerFactory.getLogger("Main")
    val cliArgs = CliArgParser.parse(args)
    CliConfigManager.createConfigIfNotExists()

    if (cliArgs.isTest) {
        println("CLI tool installed correctly")
        return
    }

    if (cliArgs.showVersion) {
        println("autocommit version ${BuildConfig.VERSION}")
        return
    }

    if (cliArgs.setDefault != null) {
        CliConfigManager.setDefaultLlmService(cliArgs.setDefault)
        logger.info("Default LLM response service set to ${cliArgs.setDefault}")
        return
    }

    if (cliArgs.setOpenaiApiKey != null) {
        CliConfigManager.setOpenaiApiKey(cliArgs.setOpenaiApiKey)
        logger.info("OpenAI API key set to ${cliArgs.setOpenaiApiKey.take(10)}...")
        return
    }

    if (cliArgs.setAnthropicApiKey != null) {
        CliConfigManager.setAnthropicApiKey(cliArgs.setAnthropicApiKey)
        logger.info("Anthropic API key is to ${cliArgs.setAnthropicApiKey.take(10)}...")
        return
    }

    if (cliArgs.setGoogleVertexProjectId != null) {
        CliConfigManager.setGoogleVertexProjectId(cliArgs.setGoogleVertexProjectId)
        logger.info("Google vertex project id set to ${cliArgs.setGoogleVertexProjectId}")
        return
    }

    if (cliArgs.setGoogleVertexLocation != null) {
        CliConfigManager.setGoogleVertexLocation(cliArgs.setGoogleVertexLocation)
        logger.info("Google vertex location set to ${cliArgs.setGoogleVertexLocation}")
        return
    }

    val defaultLlm = CliConfigManager["DEFAULT_LLM"]
    val selectedLlm = when {
        cliArgs.useLocal -> LlmType.LOCAL
        cliArgs.useOpenai -> LlmType.OPENAI
        cliArgs.useAnthropic -> LlmType.ANTHROPIC
        cliArgs.useGoogle -> LlmType.GOOGLE_VERTEX
        else -> LlmType.valueOf(defaultLlm.uppercase())
    }

    val llmProvider = LlmProviderFactory.getProvider(selectedLlm)

    val options = SummaryOptions(
        llmProvider = llmProvider,
        isPr = cliArgs.isPr || cliArgs.isPlainPr,
        withGui = !cliArgs.isPlainPr
    )

    logger.debug("Starting git changes summarization")
    GitChangesSummarizer.summarizeChanges(options)
}
