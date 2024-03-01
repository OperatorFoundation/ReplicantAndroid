package org.operatorfoundation.replicantandroid

import android.content.Context
import kotlinx.serialization.Serializable
import org.operatorfoundation.transmission.Connection
import org.operatorfoundation.transmission.ConnectionType
import org.operatorfoundation.transmission.TransmissionConnection
import java.util.logging.Logger

@Serializable
class ReplicantConfig(val serverAddress: String, val polish: PolishConfig, val toneburst: ToneBurst, val transport: String)
{
    fun connect(context: Context, logger: Logger?): Connection {
        val hostPort = serverAddress.split(":")
        val host = hostPort[0]
        val port = hostPort[1].toInt()
        val connection = TransmissionConnection(host, port, ConnectionType.TCP,null)
        return connect(context, logger, connection)
    }

    fun connect(context: Context, logger: Logger?, connection: Connection): Connection {
        toneburst.perform(connection)
        return polish.polish(connection, context, logger)
    }
}