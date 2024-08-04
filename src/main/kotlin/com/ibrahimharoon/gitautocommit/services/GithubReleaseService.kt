package com.ibrahimharoon.gitautocommit.services

import com.ibrahimharoon.gitautocommit.rest.dtos.GithubReleaseDto
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

object GithubReleaseService {
    private const val GITHUB_BASE_URL = "https://api.github.com"
    private const val GITHUB_REPO = "repos/Ibrahim-Haroon/git-autocommit-cli"
    private const val GITHUB_API_URL = "$GITHUB_BASE_URL/$GITHUB_REPO/releases/latest"
    private val restTemplate = RestTemplate()

    fun getLatestVersion(): String {
        val release = getLatestRelease()
        return release.tagName.removePrefix("v")
    }

    private fun getLatestRelease(): GithubReleaseDto {
        return restTemplate.getForObject<GithubReleaseDto>(GITHUB_API_URL)
    }
}
