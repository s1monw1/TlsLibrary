package de.swirtz.tlslib.core

import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.nio.file.Path
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
                    init(loadKeyStore(conf), conf.password?.toCharArray())
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

    private fun loadKeyStore(store: StoreConfiguration) = KeyStore.getInstance(store.fileType).apply {
        load(FileInputStream(store.storeFile?.toFile()), store.password?.toCharArray())
    }
}

@DslMarker
annotation class TlsDSLMarker

@TlsDSLMarker
data class StoreConfiguration(var algorithm: String? = null, var storeFile: Path? = null, var password: String? = null, var fileType: String = "JKS")

@TlsDSLMarker
data class SocketConfiguration(var cipherSuites: List<String>? = null, var timeout: Int? = null, var clientAuth: Boolean = false)

@TlsDSLMarker
class ProviderConfiguration {
    var kmConfig: StoreConfiguration? = null
    var tmConfig: StoreConfiguration? = null
    var socketConfig: SocketConfiguration? = null

    private fun initStoreConfig(configInit: StoreConfiguration.() -> Unit) =
            StoreConfiguration().apply(configInit)

    fun sockets(configInit: SocketConfiguration.() -> Unit) {
        this.socketConfig = SocketConfiguration().apply(configInit)
    }

    fun keyManager(configInit: StoreConfiguration.() -> Unit) {
        this.kmConfig = initStoreConfig(configInit)
    }

    fun trustManager(configInit: StoreConfiguration.() -> Unit) {
        this.tmConfig = initStoreConfig(configInit)
    }
}