package org.operatorfoundation.replicantandroid

import kotlinx.serialization.Serializable
import java.util.logging.Logger
import android.util.Base64
import org.operatorfoundation.keychainandroid.PublicKey
import org.operatorfoundation.shadow.DarkStar
import org.operatorfoundation.shadow.ShadowConfig
import org.operatorfoundation.shadow.ShadowConnection
import org.operatorfoundation.transmission.Connection

@Serializable
class PolishConfig(val serverAddress: String, val serverPublicKey: PublicKey) {
    fun polish(connection: Connection, logger: Logger?): Connection {
        val hostPort = serverAddress.split(":")
        val host = hostPort[0]
        val port = hostPort[1].toInt()
        val keyBytes = serverPublicKey.data
        println("keybytes to be put into shadow: $keyBytes")
        val keyString = Base64.encodeToString(keyBytes, Base64.DEFAULT)
        // FIXME: shadow config will eventually need to take a key not a string
        val shadowConfig = ShadowConfig(keyString, "darkstar", host, port)
        return ShadowConnection(connection, shadowConfig, logger)
    }
}