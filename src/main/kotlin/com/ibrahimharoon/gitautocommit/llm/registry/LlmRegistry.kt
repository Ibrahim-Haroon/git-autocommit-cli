package com.ibrahimharoon.gitautocommit.llm.registry

import com.ibrahimharoon.gitautocommit.llm.LlmType

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LlmRegistry(val llm: LlmType)
