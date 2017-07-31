package de.swirtz.tlslib

import de.swirtz.tlslib.api.TLSSocketFactoryProvider.socketFactory
import java.nio.file.Paths

fun main(args: Array<String>) {

    val fac = socketFactory {
        keyManager {
            storeFile = Paths.get("src/main/resources/koco-ps1.p12")
            password = "12345678"
            fileType = "pkcs12"
        }
        trustManager {
            storeFile = Paths.get("src/main/resources/server_truststore.jks")
            password = "123456"
            fileType = "jks"
        }
        sockets {
            cipherSuites = listOf("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                    "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA")
        }
    }

    val socket = fac.createSocket("www.kotlinlang.org", 443)
    println("Connected: ${socket.isConnected}")
}