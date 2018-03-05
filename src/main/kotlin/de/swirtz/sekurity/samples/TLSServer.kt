package de.swirtz.sekurity.samples

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.io.DataInputStream
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ServerSocketFactory

/**
 * Creates Socket with supplied socketFactory and waits for a client to connect
 */
class TLSServer(private val port: Int, private val socketFactory: ServerSocketFactory) {

    private var read = StringBuilder()

    private val running = AtomicBoolean(true)

    fun getLastMsg() = read.toString()

    fun start() = launch(CommonPool) {
        while (running.get()) {
            val accept = socketFactory.createServerSocket(port).accept()
            println("accepted socket $accept")
            DataInputStream(accept.getInputStream()).use { d ->
                var readUTF = d.readUTF()
                while (readUTF != null) {
                    println("Read: '$readUTF'")
                    read.append(readUTF)
                    readUTF = d.readUTF()
                }
            }
        }
    }

    fun stop() = running.set(false)


}