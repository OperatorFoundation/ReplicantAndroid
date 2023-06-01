package org.operatorfoundation.replicantandroid

import android.util.Base64
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test
import org.operatorfoundation.shadowkotlin.DarkStar
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun testReplicant() {
        val configFile = File("")
        val configText = configFile.readText(Charsets.UTF_8)
        val replicantConfig = Json.decodeFromString<ReplicantConfig>(configText)
        val replicantConnection = replicantConfig.connect(null)
        val success = replicantConnection.write("pass")
        assert(success)
        val serverBytes = replicantConnection.read(7)
        assert(serverBytes == "success".encodeToByteArray())
    }
}