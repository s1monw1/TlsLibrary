package de.swirtz.sekurity.api

import de.swirtz.sekurity.core.ProviderConfiguration
import de.swirtz.sekurity.core.TLSSocketFactoryProvider
import java.lang.reflect.ParameterizedType

val defaultTLSProtocols = listOf("TLSv1.2")

typealias DSLConfig = ProviderConfiguration.() -> Unit
/**
 * To be used for creating [SSLServerSocketFactory] instances by using the Sekurity DSL
 */
fun serverSocketFactory(protocols: List<String> = defaultTLSProtocols, configuration: DSLConfig = {}) =
        with(TLSSocketFactoryProvider(configuration)) {
            this.createServerSocketFactory(protocols)
        }

/**
 * To be used for creating [SSLSocketFactory] instances by using the Sekurity DSL
 */
fun socketFactory(protocols: List<String> = defaultTLSProtocols, configuration: DSLConfig = {}) =
        with(TLSSocketFactoryProvider(configuration)) {
            this.createSocketFactory(protocols)
        }

