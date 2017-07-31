package de.swirtz.tlslib

import de.swirtz.tlslib.api.SocketFactoryProvider.socketFactory
import java.nio.file.Paths

fun main(args: Array<String>) {

    val fac = socketFactory {
        keyManager {
            algorithm = "SunX509"
            storeFile = Paths.get("src/main/resources/koco-ps1.p12")
            password = "12345678"
            fileType = "pkcs12"
        }
        trustManager {
            algorithm = "SunX509"
            storeFile = Paths.get("src/main/resources/server_truststore.jks")
            password = "123456"
            fileType = "jks"
        }
    }

    //TODO Creation of socket has to be extended
    val socket = fac.createSocket("www.kotlinlang.org", 443)
    println("Connected: ${socket.isConnected}")
}