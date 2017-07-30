package de.swirtz.tlslib.api

import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.security.SecureRandom
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory

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
        configuration()

        LOG.debug("Creating Factory with \n$kmConfig \n$tmConfig")
        with(SSLContext.getInstance(protocol)) {
            val defaultAlgorithm = KeyManagerFactory.getDefaultAlgorithm()
            LOG.debug("Default KeyManager arlogithm: $defaultAlgorithm")
            val keyManagerFactory = KeyManagerFactory.getInstance(defaultAlgorithm)
            val trustManagerFactory = null


            this.init(null, null, SecureRandom())
            return this.socketFactory
        }
    }

}