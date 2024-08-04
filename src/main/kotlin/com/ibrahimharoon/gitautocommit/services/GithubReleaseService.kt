package com.ibrahimharoon.gitautocommit.services

import com.ibrahimharoon.gitautocommit.rest.dtos.GithubReleaseDto
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

/**
 * Service object for interacting with GitHub's Release API.
 *
 * This object provides methods to fetch information about the latest release
 * of the git-autocommit-cli project from GitHub.
 */
object GithubReleaseService {
    private const val GITHUB_BASE_URL = "https://api.github.com"
    private const val GITHUB_REPO = "repos/Ibrahim-Haroon/git-autocommit-cli"
    private const val GITHUB_API_URL = "$GITHUB_BASE_URL/$GITHUB_REPO/releases/latest"
    private val restTemplate = RestTemplate()

    /**
     * Retrieves the version number of the latest release.
     *
     * This method fetches the latest release information and extracts the version number
     * from the tag name. It assumes that the tag name follows the format "vX.Y.Z" and
     * removes the "v" prefix.
     *
     * @return The version number as a string, without the "v" prefix.
     * @throws RestClientException if there's an error communicating with the GitHub API.
     * @throws HttpClientErrorException if the API returns a 4xx status code.
     * @throws HttpServerErrorException if the API returns a 5xx status code.
     */
    fun getLatestVersion(): String {
        val release = getLatestRelease()
        return release.tagName.removePrefix("v")
    }

    /**
     * Fetches the full details of the latest release from GitHub.
     *
     * This private method makes an HTTP GET request to the GitHub API to retrieve
     * information about the latest release of the project.
     *
     * @return A [GithubReleaseDto] object containing the full details of the latest release.
     * @throws RestClientException if there's an error communicating with the GitHub API.
     * @throws HttpClientErrorException if the API returns a 4xx status code.
     * @throws HttpServerErrorException if the API returns a 5xx status code.
     */
    private fun getLatestRelease(): GithubReleaseDto {
        return restTemplate.getForObject<GithubReleaseDto>(GITHUB_API_URL)
    }
}
