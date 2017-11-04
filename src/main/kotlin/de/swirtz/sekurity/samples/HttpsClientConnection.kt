package de.swirtz.sekurity.samples

import java.net.URL
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory

/**
 * Can be used to create [HttpsURLConnection]s.
 */
class HttpsClientConnection(private val socketFactory: SSLSocketFactory, private val readTO: Int = 0,
                            private val hnVerifier: (String, SSLSession) -> Boolean =
                            { _, _ -> true }) {

    fun createHttpsUrlConnection(urlString: String): HttpsURLConnection {
        val url = URL(urlString)
        return (url.openConnection() as HttpsURLConnection).apply {
            sslSocketFactory = socketFactory
            hostnameVerifier = HostnameVerifier(hnVerifier)
            readTimeout = readTO
        }
    }

    fun readContentFromServer(urlString: String) {
        createHttpsUrlConnection(urlString).inputStream.use {
            val s = java.util.Scanner(it).useDelimiter("\\A")
            if (s.hasNext()) println(s.next())
        }
    }


}