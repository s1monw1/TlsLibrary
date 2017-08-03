package de.swirtz.tlslib.core

import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.*

/**
 * Provides JSSE connections
 *
 * https://docs.oracle.com/javase/7/docs/technotes/guides/security/jsse/JSSERefGuide.html
 */
@TlsDSLMarker
class TLSSocketFactoryProvider(init: ProviderConfiguration.() -> Unit,
                               val config: ProviderConfiguration = ProviderConfiguration().apply(init)) {

    private val LOG = LoggerFactory.getLogger(TLSSocketFactoryProvider::class.java)

    fun createSocketFactory(protocols: List<String>): SSLSocketFactory = with(createSSLContext(protocols)) {
        return ExtendedSSLSocketFactory(socketFactory, protocols.toTypedArray(),
                getOptionalCipherSuites() ?: socketFactory.defaultCipherSuites)
    }

    fun createServerSocketFactory(protocols: List<String>): SSLServerSocketFactory = with(createSSLContext(protocols)) {
        return ExtendedSSLServerSocketFactory(serverSocketFactory, protocols.toTypedArray(),
                getOptionalCipherSuites() ?: serverSocketFactory.defaultCipherSuites)
    }

    private fun getOptionalCipherSuites() = config.socketConfig?.cipherSuites?.toTypedArray()

    private fun createSSLContext(protocols: List<String>): SSLContext {
        if (protocols.isEmpty()) {
            throw IllegalArgumentException("At least one protocol must be provided.")
        }
        return SSLContext.getInstance(protocols[0]).apply {
            val kmConfig = config.kmConfig
            val tmConfig = config.tmConfig
            LOG.debug("Creating Factory with \n$kmConfig \n$tmConfig")

            val keyManagerFactory = kmConfig?.let { conf ->
                val defaultAlgorithm = KeyManagerFactory.getDefaultAlgorithm()
                LOG.debug("KeyManager default algorithm: $defaultAlgorithm")
                KeyManagerFactory.getInstance(conf.algorithm ?: defaultAlgorithm).apply {
                    init(loadKeyStore(conf), conf.password)
                }
            }
            val trustManagerFactory = tmConfig?.let { conf ->
                val defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
                LOG.debug("TrustManager default algorithm: $defaultAlgorithm")
                TrustManagerFactory.getInstance(conf.algorithm ?: defaultAlgorithm).apply {
                    init(loadKeyStore(conf))
                }
            }

            init(keyManagerFactory?.keyManagers, trustManagerFactory?.trustManagers, SecureRandom())
        }

    }

    private fun loadKeyStore(store: Store) = KeyStore.getInstance(store.fileType).apply {
        load(FileInputStream(store.name), store.password)
    }
}

@DslMarker
annotation class TlsDSLMarker

@TlsDSLMarker
data class SocketConfiguration(var cipherSuites: List<String>? = null, var timeout: Int? = null, var clientAuth: Boolean = false)

@TlsDSLMarker
class Store(val name: String) {
    var algorithm: String? = null
    var password: CharArray? = null
    var fileType: String = "JKS"

    infix fun withPass(pass: String) = this.apply { password = pass.toCharArray() }

    infix fun beingA(type: String) = apply {
        fileType = type
    }

    infix fun using(algo: String) = this.apply { algorithm = algo }

}

@TlsDSLMarker
class ProviderConfiguration {

    var kmConfig: Store? = null
    var tmConfig: Store? = null
    var socketConfig: SocketConfiguration? = null

    fun open(name: String) = Store(name)

    fun sockets(configInit: SocketConfiguration.() -> Unit) {
        this.socketConfig = SocketConfiguration().apply(configInit)
    }

    fun keyManager(store: () -> Store) {
        this.kmConfig = store()
    }

    fun trustManager(store: () -> Store) {
        this.tmConfig = store()
    }
}