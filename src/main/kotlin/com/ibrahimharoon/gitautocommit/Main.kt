package com.ibrahimharoon.gitautocommit

import com.ibrahimharoon.gitautocommit.cli.CliArgParser
import com.ibrahimharoon.gitautocommit.cli.CliConfig
import com.ibrahimharoon.gitautocommit.cli.CommitConfig
import com.ibrahimharoon.gitautocommit.cli.GitOperations
import io.github.cdimascio.dotenv.Dotenv
import org.slf4j.LoggerFactory
import org.slf4j.Logger

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

    val dotenv = Dotenv.configure()
        .directory(System.getProperty("user.home") + "/.local/bin")
        .filename("autocommit-config.env")
        .load()

    val defaultLlm = dotenv["DEFAULT_LLM"] ?: "google"
    val useLocal = cliArgs.useLocal || (!cliArgs.useOpenai && !cliArgs.useGoogle && defaultLlm == "local")
    val useOpenai = cliArgs.useOpenai || (!cliArgs.useLocal && !cliArgs.useGoogle && defaultLlm == "openai")
    val useGoogle = cliArgs.useGoogle || (!cliArgs.useLocal && !cliArgs.useOpenai && defaultLlm == "google")
    val isPlainPr = cliArgs.isPlainPr
    val isPr = cliArgs.isPr || isPlainPr

    val config = CommitConfig(
        useLocal,
        useOpenai,
        useGoogle,
        isPr,
        isPlainPr
    )

    logger.debug("Starting git autocommit")
    GitOperations.performCommit(config)
}