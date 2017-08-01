package de.swirtz.tlslib.server

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.io.DataInputStream
import java.util.concurrent.atomic.AtomicReference
import javax.net.ServerSocketFactory

class TLSServer(val port: Int, val socketFactory: ServerSocketFactory) {

    private var lastMsg = AtomicReference<String>()
    fun getLastMsg() = lastMsg.get()
    fun start() {
        launch(CommonPool) {
            val accept = socketFactory.createServerSocket(port).accept()
            println("accepted socket $accept")
            accept.getInputStream().use {
                DataInputStream(it).use { d->
                    val readUTF = d.readUTF()
                    println("Read $readUTF")

                    lastMsg.set(readUTF)

                }
            }
        }
    }
}