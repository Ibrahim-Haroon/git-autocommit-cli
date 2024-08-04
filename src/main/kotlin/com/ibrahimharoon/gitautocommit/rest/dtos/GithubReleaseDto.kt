package com.ibrahimharoon.gitautocommit.rest.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents the response structure from the GitHub Releases API.
 *
 * This data class encapsulates all the information provided by GitHub's API for a specific release.
 * It includes details about the release itself, the author, and any associated assets.
 *
 * @property url The API URL for this release.
 * @property assetsUrl The API URL to retrieve this release's assets.
 * @property uploadUrl The API URL for uploading assets to this release.
 * @property htmlUrl The URL to view this release on GitHub.
 * @property id The unique identifier for this release.
 * @property author Information about the user who created this release.
 * @property nodeId The GraphQL node ID for this release.
 * @property tagName The git tag associated with this release.
 * @property targetCommitish The commitish value that determines where the Git tag is created from.
 * @property name The name of the release.
 * @property isDraft Indicates whether this is a draft release.
 * @property isPrerelease Indicates whether this is a prerelease.
 * @property createdAt The date and time this release was created.
 * @property publishedAt The date and time this release was published.
 * @property assets A list of assets attached to this release.
 * @property tarballUrl The URL to download the tarball version of the release.
 * @property zipballUrl The URL to download the zipball version of the release.
 * @property body The description of the release, typically containing release notes.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GithubReleaseDto(
    @JsonProperty("url") val url: String,
    @JsonProperty("assets_url") val assetsUrl: String,
    @JsonProperty("upload_url") val uploadUrl: String,
    @JsonProperty("html_url") val htmlUrl: String,
    @JsonProperty("id") val id: Long,
    @JsonProperty("author") val author: Author,
    @JsonProperty("node_id") val nodeId: String,
    @JsonProperty("tag_name") val tagName: String,
    @JsonProperty("target_commitish") val targetCommitish: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("draft") val isDraft: Boolean,
    @JsonProperty("prerelease") val isPrerelease: Boolean,
    @JsonProperty("created_at") val createdAt: String,
    @JsonProperty("published_at") val publishedAt: String,
    @JsonProperty("assets") val assets: List<Asset>,
    @JsonProperty("tarball_url") val tarballUrl: String,
    @JsonProperty("zipball_url") val zipballUrl: String,
    @JsonProperty("body") val body: String
) {
    /**
     * Represents the author of a GitHub release.
     *
     * @property login The username of the author.
     * @property id The unique identifier for this user.
     * @property nodeId The GraphQL node ID for this user.
     * @property avatarUrl The URL of the user's avatar image.
     * @property url The API URL for this user.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Author(
        @JsonProperty("login") val login: String,
        @JsonProperty("id") val id: Long,
        @JsonProperty("node_id") val nodeId: String,
        @JsonProperty("avatar_url") val avatarUrl: String,
        @JsonProperty("url") val url: String
    )

    /**
     * Represents an asset attached to a GitHub release.
     *
     * Assets are typically downloadable files associated with the release.
     *
     * @property url The API URL for this asset.
     * @property id The unique identifier for this asset.
     * @property nodeId The GraphQL node ID for this asset.
     * @property name The name of the file.
     * @property label An alternate short description of the asset. Used in place of the filename.
     * @property uploader The user who uploaded the asset.
     * @property contentType The MIME type of the asset.
     * @property state The state of the asset. Usually "uploaded".
     * @property size The size of the asset in bytes.
     * @property downloadCount The number of times this asset has been downloaded.
     * @property createdAt The date and time this asset was created.
     * @property updatedAt The date and time this asset was last updated.
     * @property browserDownloadUrl The URL to download this asset directly from a browser.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Asset(
        @JsonProperty("url") val url: String,
        @JsonProperty("id") val id: Long,
        @JsonProperty("node_id") val nodeId: String,
        @JsonProperty("name") val name: String,
        @JsonProperty("label") val label: String?,
        @JsonProperty("uploader") val uploader: Author,
        @JsonProperty("content_type") val contentType: String,
        @JsonProperty("state") val state: String,
        @JsonProperty("size") val size: Long,
        @JsonProperty("download_count") val downloadCount: Int,
        @JsonProperty("created_at") val createdAt: String,
        @JsonProperty("updated_at") val updatedAt: String,
        @JsonProperty("browser_download_url") val browserDownloadUrl: String
    )
}
