package com.ibrahimharoon.gitautocommit.rest.dtos

data class AnthropicLlmResponseDto(
    val content: List<Content>
) {
    data class Content(
        val text: String,
        val type: String
    )
}