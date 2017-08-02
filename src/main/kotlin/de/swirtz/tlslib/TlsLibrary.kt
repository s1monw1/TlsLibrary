package de.swirtz.tlslib

import de.swirtz.tlslib.api.socketFactory
import java.nio.file.Paths


fun main(args: Array<String>) {
    val fac = socketFactory {
        keyManager {
            storeFile = Paths.get("certsandstores/clientkeystore")
            password = "123456"
            fileType = "jks"
        }
        trustManager {
            storeFile = Paths.get("certsandstores/myTruststore")
            password = "123456"
            fileType = "jks"
        }
        sockets {
            cipherSuites = listOf("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                    "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA")
            timeout = 10_000
        }
    }

    val socket = fac.createSocket("192.168.3.200", 9443)
//    HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
//    HttpsURLConnection.setDefaultSSLSocketFactory(fac)
//    val url = URL("xy")
//    val con = url.openConnection() as HttpsURLConnection
//    con.connect()
    println("Connected: ${socket.isConnected}")
}