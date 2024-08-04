package com.ibrahimharoon.gitautocommit.llm.factory

import com.ibrahimharoon.gitautocommit.llm.LlmType
import com.ibrahimharoon.gitautocommit.llm.provider.AnthropicLlmProvider
import com.ibrahimharoon.gitautocommit.llm.provider.GoogleVertexLlmProvider
import com.ibrahimharoon.gitautocommit.llm.provider.LlmProvider
import com.ibrahimharoon.gitautocommit.llm.provider.LocalLlmProvider
import com.ibrahimharoon.gitautocommit.llm.provider.OpenAiLlmProvider

/**
 * This object is used to instantiate an LLM provider
 */
object LlmProviderFactory {
    /**
     * This method is used to instantiate the correct LLM provider
     *
     * @param llm: The selected LLM from user
     * @return instantiated LLM provider
     */
    fun getProvider(llm: LlmType): LlmProvider {
        return when (llm) {
            LlmType.LOCAL -> LocalLlmProvider()
            LlmType.OPENAI -> OpenAiLlmProvider()
            LlmType.ANTHROPIC -> AnthropicLlmProvider()
            LlmType.GOOGLE_VERTEX -> GoogleVertexLlmProvider()
        }
    }
}
