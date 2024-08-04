package com.ibrahimharoon.gitautocommit.cli

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.Properties

/**
 * Manages the configuration for the git-autocommit CLI tool.
 *
 * This object is responsible for reading, writing, and managing the configuration
 * settings for the application. It handles the persistence of various settings
 * such as API keys, default LLM service, and other configurable options.
 */
object CliConfigManager {
    private val logger: Logger = LoggerFactory.getLogger("Main")
    private val CONFIG_FILE_NAME = System.getProperty("user.home") + "/.local/bin/autocommit-config.env"
    private val configFile = File(CONFIG_FILE_NAME)
    private val properties = Properties()

    init {
        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
            configFile.createNewFile()
        }
        properties.load(configFile.inputStream())
    }

    /**
     * Creates the configuration file if it doesn't exist.
     * This method ensures that the configuration file is available for use.
     */
    fun createConfigIfNotExists() { return }

    /**
     * Retrieves a configuration value.
     *
     * @param key The key of the configuration value to retrieve.
     * @return The value associated with the given key, or null if not found.
     */
    operator fun get(key: String): String {
        val `val` = properties.getProperty(key)
        if (`val` == null) {
            logger.error("Couldn't find $key in $properties")
            return ""
        }

        return `val`
    }

    fun setDefaultLlmService(llm: String) {
        properties.setProperty("DEFAULT_LLM", llm)
        properties.store(configFile.outputStream(), null)
    }

    fun setOpenaiApiKey(apiKey: String) {
        properties.setProperty("OPENAI_API_KEY", apiKey)
        properties.store(configFile.outputStream(), null)
    }

    fun setAnthropicApiKey(apiKey: String) {
        properties.setProperty("ANTHROPIC_API_KEY", apiKey)
        properties.store(configFile.outputStream(), null)
    }

    fun setGoogleVertexProjectId(googleVertexProjectId: String) {
        properties.setProperty("GOOGLE_VERTEX_PROJECT_ID", googleVertexProjectId)
        properties.store(configFile.outputStream(), null)
    }

    fun setGoogleVertexLocation(googleVertexLocation: String) {
        properties.setProperty("GOOGLE_VERTEX_LOCATION", googleVertexLocation)
        properties.store(configFile.outputStream(), null)
    }
}
