package de.swirtz.tlslib.api

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
annotation class CustomDSLMarker

@CustomDSLMarker
object SocketFactoryProvider {

    private val LOG = LoggerFactory.getLogger(SocketFactoryProvider::class.java)

    var kmConfig: StoreConfiguration? = null
    var tmConfig: StoreConfiguration? = null

    @CustomDSLMarker
    data class StoreConfiguration(var algorithm: String? = null, var storeFile: Path? = null, var password: String? = null, var fileType: String? = null)

    private fun initConfig(configInit: StoreConfiguration.() -> Unit) =
            StoreConfiguration().apply(configInit)

    fun keyManager(configInit: StoreConfiguration.() -> Unit) {
        this.kmConfig = initConfig(configInit)
    }

    fun trustManager(configInit: StoreConfiguration.() -> Unit) {
        this.tmConfig = initConfig(configInit)
    }

    fun socketFactory(protocol: String = "TLSv1.2", configuration: SocketFactoryProvider.() -> Unit): SSLSocketFactory {
        this.apply(configuration)
        LOG.debug("Creating Factory with \n$kmConfig \n$tmConfig")

        with(SSLContext.getInstance(protocol)) {

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

            this.init(keyManagerFactory?.keyManagers, trustManagerFactory?.trustManagers, SecureRandom())
            return this.socketFactory
        }
    }

    private fun loadKeyStore(store: StoreConfiguration): KeyStore {
        val keyStore = KeyStore.getInstance(store.fileType)
        keyStore.load(FileInputStream(store.storeFile?.toFile()), store.password?.toCharArray())
        return keyStore
    }


}