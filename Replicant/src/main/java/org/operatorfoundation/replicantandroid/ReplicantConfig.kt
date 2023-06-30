package org.operatorfoundation.replicantandroid

import kotlinx.serialization.Serializable
import org.operatorfoundation.transmission.Connection
import org.operatorfoundation.transmission.ConnectionType
import org.operatorfoundation.transmission.TransmissionConnection
import java.util.logging.Logger

@Serializable
class ReplicantConfig(val serverAddress: String, val polish: PolishConfig, val toneBurst: Toneburst, val transport: String) {
    fun connect(logger: Logger?): Connection {
        val hostPort = serverAddress.split(":")
        val host = hostPort[0]
        val port = hostPort[1].toInt()
        val connection = TransmissionConnection(host, port, ConnectionType.TCP,null)
        toneBurst.starburst.perform(connection)
        return polish.polish(connection, logger)
    }
}