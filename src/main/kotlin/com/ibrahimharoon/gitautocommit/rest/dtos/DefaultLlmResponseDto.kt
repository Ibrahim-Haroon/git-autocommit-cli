package com.ibrahimharoon.gitautocommit.rest.dtos

data class DefaultLlmResponseDto(
    val choices: List<Choice>
) {
    data class Choice(
        val message: Message
    ) {
        data class Message(
            val content: String
        )
    }
}