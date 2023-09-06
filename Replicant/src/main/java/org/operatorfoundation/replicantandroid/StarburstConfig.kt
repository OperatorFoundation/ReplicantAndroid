package org.operatorfoundation.replicantandroid

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.operatorfoundation.ghostwriterandroid.*
import org.operatorfoundation.transmission.*

sealed interface ToneBurst {
    fun perform(connection: Connection)
}

@Serializable
@SerialName("starburst")
class Starburst(var mode: String): ToneBurst {
    override fun perform(connection: Connection) {
        val firstClientListen = ListenTemplate(Template("220 $1 SMTP service ready\r\n"), arrayOf(ExtractionPattern("^([a-zA-Z0-9.-]+)", Types.string)), 253, Int.MAX_VALUE)
        if (firstClientListen == null) {
            throw Exception("first listen failed")
        }

        listen(firstClientListen, connection)
        speak(Template("EHLO $1\r\n"), arrayOf(Detail("mail.imc.org")), connection)
        val secondClientListen = ListenTemplate(Template("$1\r\n"), arrayOf(ExtractionPattern("250 (STARTTLS)", Types.string)), 253, 10)
        if (secondClientListen == null) {
            throw Exception("second listen failed")
        }

        // FIXME: It's not liking this listen even though all of the correct things are going through wireshark
        listen(secondClientListen, connection)
        speak("STARTTLS\r\n", connection)
        val thirdClientListen = ListenTemplate(Template("$1\r\n"), arrayOf(ExtractionPattern("^(.+)\r\n", Types.string)), 253, 10)
        if (thirdClientListen == null) {
            throw Exception("third listen failed")
        }

        listen(thirdClientListen, connection)
    }

    // TODO: eventually add threading for a timeout
    fun listen(template: ListenTemplate, connection: Connection) {
        var buffer: ByteArray = byteArrayOf()
        while(buffer.size < template.maxSize) {
            val byte = connection.read(1)
            if (byte == null) {
                return
            }

            buffer += byte
            val string = buffer.decodeToString()
            val details = Parse(template.template, template.patterns, string)
            if (details != null) {
                return
            }
        }
    }

    fun speak(template: Template, details: Array<Detail>, connection: Connection) {
        val string = Generate(template, details)
        if (string == null) {
            print("failed to generate string from template")
            return
        }

        if (!connection.write(string)) {
            throw Exception("write failed")
        }
    }

    fun speak(string: String, connection: Connection) {
        if (!connection.write(string)) {
            throw Exception("write failed")
        }
    }
}

@Serializable
class ListenTemplate(val template: Template, val patterns: Array<ExtractionPattern>, val maxSize: Int, val maxTimeoutSeconds: Int)

