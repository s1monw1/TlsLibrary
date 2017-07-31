package de.swirtz.tlslib.api

import de.swirtz.tlslib.core.ExtendedSSLSocketFactory
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.nio.file.Path
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

@DslMarker
annotation class TlsDSLMarker

/**
 * Provides JSSE connections
 *
 * https://docs.oracle.com/javase/7/docs/technotes/guides/security/jsse/JSSERefGuide.html
 */
@TlsDSLMarker
object TLSSocketFactoryProvider {

    private val LOG = LoggerFactory.getLogger(TLSSocketFactoryProvider::class.java)

    var kmConfig: StoreConfiguration? = null
    var tmConfig: StoreConfiguration? = null
    var socketConfig: SocketConfiguration? = null

    @TlsDSLMarker
    data class StoreConfiguration(var algorithm: String? = null, var storeFile: Path? = null, var password: String? = null, var fileType: String? = null)

    @TlsDSLMarker
    data class SocketConfiguration(var cipherSuites: List<String>? = null, var timeout: Int? = null)

    private fun initStoreConfig(configInit: StoreConfiguration.() -> Unit) =
            StoreConfiguration().apply(configInit)

    private fun initSocketConfig(configInit: SocketConfiguration.() -> Unit) =
            SocketConfiguration().apply(configInit)

    fun sockets(configInit: SocketConfiguration.() -> Unit) {
        this.socketConfig = initSocketConfig(configInit)
    }

    fun keyManager(configInit: StoreConfiguration.() -> Unit) {
        this.kmConfig = initStoreConfig(configInit)
    }

    fun trustManager(configInit: StoreConfiguration.() -> Unit) {
        this.tmConfig = initStoreConfig(configInit)
    }

    fun socketFactory(protocols: List<String> = listOf("TLSv1.2"),
                      configuration: (TLSSocketFactoryProvider.() -> Unit)? = null): SSLSocketFactory {

        configuration?.invoke(this)
        LOG.debug("Creating Factory with \n$kmConfig \n$tmConfig")

        val sslContext = SSLContext.getInstance(protocols[0]).apply {
            val keyManagerFactory = kmConfig?.let {
                val defaultAlgorithm = KeyManagerFactory.getDefaultAlgorithm()
                LOG.debug("KeyManager default algorithm: $defaultAlgorithm")
                val instance = KeyManagerFactory.getInstance(kmConfig?.algorithm ?: defaultAlgorithm)
                val store = kmConfig as StoreConfiguration
                instance.init(loadKeyStore(store), store.password?.toCharArray())
                instance
            }
            val trustManagerFactory = tmConfig?.let {
                val defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
                LOG.debug("TrustManager default algorithm: $defaultAlgorithm")
                val instance = TrustManagerFactory.getInstance(tmConfig?.algorithm ?: defaultAlgorithm)
                instance.init(loadKeyStore(kmConfig as StoreConfiguration))
                instance
            }

            init(keyManagerFactory?.keyManagers, trustManagerFactory?.trustManagers, SecureRandom())
        }
        val socketFactory = sslContext.socketFactory
        val ciphers = socketConfig?.cipherSuites?.toTypedArray()
        return ExtendedSSLSocketFactory(socketFactory, protocols.toTypedArray(), ciphers ?: socketFactory.defaultCipherSuites)
    }

    private fun loadKeyStore(store: StoreConfiguration) = KeyStore.getInstance(store.fileType).apply {
        load(FileInputStream(store.storeFile?.toFile()), store.password?.toCharArray())
    }


}