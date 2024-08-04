package com.ibrahimharoon.gitautocommit.rest.dtos

/**
 * Represents the response structure from the Google Vertex AI API.
 *
 * @property candidates A list of Candidate objects representing possible responses.
 */
data class GoogleVertexResponseDto(
    val candidates: List<Candidate>
) {
    /**
     * Represents a single candidate response from the Google Vertex AI.
     *
     * @property content The Content object containing the response details.
     */
    data class Candidate(
        val content: Content
    ) {
        /**
         * Represents the content of a candidate response.
         *
         * @property parts A list of Part objects that make up the content.
         */
        data class Content(
            val parts: List<Part>
        ) {
            /**
             * Represents a single part of the content.
             *
             * @property text The actual text content of this part.
             */
            data class Part(
                val text: String
            )
        }
    }
}
