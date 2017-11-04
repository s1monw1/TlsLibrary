package de.swirtz.sekurity.core

import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLServerSocketFactory
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class ExtendedSSLSocketFactory(private val delegate: SSLSocketFactory, private val protocols: Array<String>,
                               private val cipherSuites: Array<String> = delegate.defaultCipherSuites,
                               private var timeout: Int = 0) : SSLSocketFactory() {

    override fun createSocket(p0: Socket?, p1: String?, p2: Int, p3: Boolean): Socket =
            extend(delegate.createSocket(p0, p1, p2, p3) as SSLSocket)

    override fun createSocket(p0: String?, p1: Int): Socket = extend(delegate.createSocket(p0, p1) as SSLSocket)

    override fun createSocket(p0: String?, p1: Int, p2: InetAddress?, p3: Int): Socket =
            extend(delegate.createSocket(p0, p1, p2, p3) as SSLSocket)

    override fun createSocket(p0: InetAddress?, p1: Int): Socket = extend(delegate.createSocket(p0, p1) as SSLSocket)

    override fun createSocket(p0: InetAddress?, p1: Int, p2: InetAddress?, p3: Int): Socket =
            extend(delegate.createSocket(p0, p1, p2, p3) as SSLSocket)

    override fun getDefaultCipherSuites(): Array<String> = protocols
    override fun getSupportedCipherSuites(): Array<String> = protocols

    private fun extend(socket: SSLSocket): Socket = socket.apply {
        enabledCipherSuites = cipherSuites
        enabledProtocols = protocols
        soTimeout = timeout
    }
}


class ExtendedSSLServerSocketFactory(private val delegate: SSLServerSocketFactory, private val protocols: Array<String>,
                                     private val cipherSuites: Array<String> = delegate.defaultCipherSuites,
                                     private var timeout: Int = 0, private var clientAuth: Boolean = false) : SSLServerSocketFactory() {
    override fun createServerSocket(p0: Int): ServerSocket = extend(delegate.createServerSocket(p0) as SSLServerSocket)

    override fun createServerSocket(p0: Int, p1: Int): ServerSocket =
            extend(delegate.createServerSocket(p0, p1) as SSLServerSocket)

    override fun createServerSocket(p0: Int, p1: Int, p2: InetAddress?): ServerSocket =
            extend(delegate.createServerSocket(p0, p1, p2) as SSLServerSocket)

    override fun getDefaultCipherSuites(): Array<String> = protocols
    override fun getSupportedCipherSuites(): Array<String> = protocols

    private fun extend(socket: SSLServerSocket): SSLServerSocket = socket.apply {
        needClientAuth = clientAuth
        enabledCipherSuites = cipherSuites
        enabledProtocols = protocols
        soTimeout = timeout
    }
}