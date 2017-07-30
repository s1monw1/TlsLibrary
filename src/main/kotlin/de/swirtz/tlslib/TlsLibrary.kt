package de.swirtz.tlslib

import de.swirtz.tlslib.api.SocketFactoryProvider.socketFactory
import java.nio.file.Paths

fun main(args: Array<String>) {

    val fac = socketFactory {
        keyManager {
            algorithm = "SunX509"
            storeFile = Paths.get("src/main/resources/mykeystore.x")
            password = "123456"
            fileType = "jks"
        }
        trustManager {
            algorithm = "SunX509"
            storeFile = Paths.get("src/main/resources/truststore.x")
            password = "123456"
            fileType = "jks"
        }
    }

    val socket = fac.createSocket("www.kotlinlang.org", 443)
    println("Connected: ${socket.isConnected}")
}