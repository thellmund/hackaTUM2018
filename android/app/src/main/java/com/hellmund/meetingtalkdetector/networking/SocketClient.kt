package com.hellmund.meetingtalkdetector.networking

import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import org.jetbrains.anko.doAsync
import java.net.URI

class SocketClient(val uri: URI, val listener: Listener) {

    private var webSocketClient: WebSocketClient? = null

    val isConnected: Boolean
        get() = webSocketClient?.isOpen ?: false

    init {
        webSocketClient = object : WebSocketClient(uri, Draft_6455()) {
            override fun onOpen(handshakeData: ServerHandshake?) {
                listener.onSocketOpened()
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                listener.onSocketClosed()
            }

            override fun onMessage(message: String?) {
                message?.let {
                    listener.onSocketMessage(it)
                }
            }

            override fun onError(e: Exception?) {
                listener.onSocketError(e)
            }
        }

        webSocketClient?.isTcpNoDelay = true
    }

    fun connect() {
        webSocketClient?.connect()
    }

    fun sendAsync(message: String) {
        doAsync {
            webSocketClient?.send(message)
        }
    }

    fun close() {
        webSocketClient?.closeBlocking()
    }

    interface Listener {
        fun onSocketOpened()
        fun onSocketClosed()
        fun onSocketMessage(message: String)
        fun onSocketError(e: Exception?)
    }

}
