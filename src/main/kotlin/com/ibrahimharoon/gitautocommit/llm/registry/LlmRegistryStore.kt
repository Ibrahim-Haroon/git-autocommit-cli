package com.ibrahimharoon.gitautocommit.llm.registry

import com.ibrahimharoon.gitautocommit.llm.LlmType
import com.ibrahimharoon.gitautocommit.llm.provider.LlmProvider
import org.reflections.Reflections
import kotlin.reflect.KClass

object LlmRegistryStore {
    private val providers = hashMapOf<LlmType, KClass<out LlmProvider>>()

    fun register(type: LlmType, providerClass: KClass<out LlmProvider>) {
        providers[type] = providerClass
    }

    fun getProvider(type: LlmType): LlmProvider {
        val providerClass = providers[type]
            ?: throw IllegalArgumentException("No provider registered for type: $type")

        return providerClass.java.getDeclaredConstructor().newInstance()
    }
}

inline fun <reified T : LlmProvider> LlmRegistryStore.register(type: LlmType) {
    register(type, T::class)
}

@Suppress("UNCHECKED_CAST")
fun registerAnnotatedProviders() {
    val reflections = Reflections("com.ibrahimharoon.gitautocommit.llm.provider")
    val annotatedClasses = reflections.getTypesAnnotatedWith(LlmRegistry::class.java)

    for (clazz in annotatedClasses) {
        val kotlinClass = clazz.kotlin
        if (LlmProvider::class.java.isAssignableFrom(clazz)) {
            val annotation = clazz.getAnnotation(LlmRegistry::class.java)
            val providerClass = kotlinClass as KClass<out LlmProvider>
            LlmRegistryStore.register(annotation.llm, providerClass)
        } else {
            throw IllegalStateException("Class ${clazz.name} is annotated with @LlmRegistry but does not implement LlmProvider")
        }
    }
}
