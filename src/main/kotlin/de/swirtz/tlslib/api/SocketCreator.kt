package de.swirtz.tlslib.api

import de.swirtz.tlslib.core.ProviderConfiguration
import de.swirtz.tlslib.core.TLSSocketFactoryProvider

val defaultTLSProtocols = listOf("TLSv1.2")

fun serverSocketFactory(protocols: List<String> = defaultTLSProtocols,
                        configuration: ProviderConfiguration.() -> Unit = {}) =
        with(TLSSocketFactoryProvider(configuration)) {
            this.createServerSocketFactory(protocols)
        }

fun socketFactory(protocols: List<String> = defaultTLSProtocols,
                  configuration: ProviderConfiguration.() -> Unit = {}) =
        with(TLSSocketFactoryProvider(configuration)) {
            this.createSocketFactory(protocols)
        }