package de.swirtz.sekurity.samples

import de.swirtz.sekurity.api.socketFactory


fun main(args: Array<String>) {
    val fac = socketFactory {
        keyManager {
            open("certsandstores/clientkeystore") withPass "123456" ofType "jks"
        }
        trustManager {
            open("certsandstores/myTruststore") withPass "123456" ofType "jks"
        }
        sockets {
            cipherSuites = listOf("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                    "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA")
            timeout = 10_000
        }
    }

    val socket = fac.createSocket("192.168.3.200", 9443)
    println("Connected: ${socket.isConnected}")
}