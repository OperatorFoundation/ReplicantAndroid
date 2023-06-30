package org.operatorfoundation.replicantandroid

import kotlinx.serialization.Serializable
import org.operatorfoundation.ghostwriterandroid.*
import org.operatorfoundation.transmission.Connection

@Serializable
class StarburstConfig(val mode: String) {
    fun perform(connection: Connection) {
        if (mode != "SMTPClient") {
            throw Exception("Error: Starburst does not support server mode on Android")
        }
//        guard let firstClientListen = ListenTemplate(Template("220 $1 SMTP service ready\r\n"), patterns: [ExtractionPattern("^([a-zA-Z0-9.-]+)", .string)], maxSize: 253, maxTimeoutSeconds: Int.max) else {
//            throw StarburstError.listenFailed
//        }
        val firstClientListen = ListenTemplate(Template("220 $1 SMTP service ready\r\n"), arrayOf(ExtractionPattern("^([a-zA-Z0-9.-]+)", Types.string)), 253, Int.MAX_VALUE)
        if (firstClientListen == null) {
            throw Exception("first listen failed")
        }
//        let _ = try listen(template: firstClientListen)
        listen(firstClientListen, connection)
//        try speak(template: Template("EHLO $1\r\n"), details: [Detail.string("mail.imc.org")])
        speak(Template("EHLO $1\r\n"), arrayOf(Detail("mail.imc.org")), connection)
//        guard let secondClientListen = ListenTemplate(Template("$1\r\n"), patterns: [ExtractionPattern("250 (STARTTLS)", .string)], maxSize: 253, maxTimeoutSeconds: 10) else {
//            throw StarburstError.listenFailed
//        }
        val secondClientListen = ListenTemplate(Template("$1\r\n"), arrayOf(ExtractionPattern("250 (STARTTLS)", Types.string)), 253, 10)
        if (secondClientListen == null) {
            throw Exception("second listen failed")
        }
//        _ = try listen(template: secondClientListen)
        listen(secondClientListen, connection)
//        try speak(string: "STARTTLS\r\n")
        speak("STARTTLS\r\n", connection)
//        guard let thirdClientListen = ListenTemplate(Template("$1\r\n"), patterns: [ExtractionPattern("^(.+)\r\n", .string)], maxSize: 253, maxTimeoutSeconds: 10) else {
//            throw StarburstError.listenFailed
//        }
        val thirdClientListen = ListenTemplate(Template("$1\r\n"), arrayOf(ExtractionPattern("^(.+)\r\n", Types.string)), 253, 10)
        if (thirdClientListen == null) {
            throw Exception("third listen failed")
        }
//        _ = try listen(template: thirdClientListen)
        listen(thirdClientListen, connection)
    }

    // TODO: eventually add threading for a timeout
    fun listen(template: ListenTemplate, connection: Connection) {
//            var buffer = Data()
        var buffer: ByteArray = byteArrayOf()
//            while buffer.count < template.maxSize && running
//            {
        while(buffer.size < template.maxSize) {
//                guard let byte = connection.read(size: 1) else
//                {
//                    resultQueue.enqueue(element: nil)
//                    lock.signal()
//                    return
//                }
            val byte = connection.read(1)
            if (byte == null) {
                return
            }
//                buffer.append(byte)
            buffer += byte
//                guard let string = String(data: buffer, encoding: .utf8) else
//                {
//                    // This could fail because we're in the middle of a UTF8 rune.
//                    continue
//                }
            val string = buffer.decodeToString()
//                do
//                {
//                    let details = try Ghostwriter.parse(template.template, template.patterns, string)
            val details = Parse(template.template, template.patterns, string)
            return
        }
    }

    fun speak(template: Template, details: Array<Detail>, connection: Connection) {
//        let string = try Ghostwriter.generate(template, details)
        val string = Generate(template, details)
        if (string == null) {
            print("failed to generate string from template")
            return
        }
//            guard connection.write(string: string) else
//            {
//                throw StarburstError.writeFailed
//            }
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

