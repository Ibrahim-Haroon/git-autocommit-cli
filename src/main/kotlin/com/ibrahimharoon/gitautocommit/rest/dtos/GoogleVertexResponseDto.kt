package com.ibrahimharoon.gitautocommit.rest.dtos

data class GoogleVertexResponseDto(
    val candidates: List<Candidate>
) {
    data class Candidate(
        val content: Content
    ) {
        data class Content(
            val parts: List<Part>
        ) {
            data class Part(
                val text: String
            )
        }
    }
}

