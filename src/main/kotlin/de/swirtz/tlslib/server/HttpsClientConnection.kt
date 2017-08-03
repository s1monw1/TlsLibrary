package de.swirtz.tlslib.server

import java.net.URL
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory

/**
 * Creates Socket with supplied createSocketFactory and waits for a client to connect
 */
class HttpsClientConnection(private val socketFactory: SSLSocketFactory, private val _readTimeout: Int = 0,
                            private val _hostnameVerifier: (String, SSLSession) -> Boolean = { _, _ -> true }) {

    fun createHttpsUrlConnection(urlString: String) = (URL(urlString).openConnection() as HttpsURLConnection).apply {
        sslSocketFactory = socketFactory
        hostnameVerifier = HostnameVerifier { var1, var2 -> _hostnameVerifier.invoke(var1, var2) }
        readTimeout = _readTimeout
    }

    fun readContentFromServer(urlString: String) {
        createHttpsUrlConnection(urlString).inputStream.use {
            val s = java.util.Scanner(it).useDelimiter("\\A")
            if (s.hasNext()) println(s.next())
        }
    }

}