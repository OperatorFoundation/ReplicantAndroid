package org.operatorfoundation.replicantandroid

import org.junit.Test

import org.junit.Assert.*
import android.util.Base64
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.operatorfoundation.shadowkotlin.DarkStar

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun testReplicant() {
        mockStatic(Base64::class.java)
        `when`(Base64.encode(any(), anyInt())).thenAnswer { invocation ->
            java.util.Base64.getMimeEncoder().encode(invocation.arguments[0] as ByteArray)
        }

        `when`(Base64.decode(anyString(), anyInt())).thenAnswer { invocation ->
            java.util.Base64.getMimeDecoder().decode(invocation.arguments[0] as String)
        }

        val keyString = "AuGiYf7ZpZS1XpekBHaIcdcWfYPsRB3\\/pF3K3qNNlrPh"
        val keyBytes = Base64.decode(keyString, Base64.DEFAULT)
        val publicKey = DarkStar.bytesToPublicKey(keyBytes)
        val polishConfig = PolishConfig("127.0.0.1:2222", publicKey)
        val starburstConfig = StarburstConfig(SMTPClient())
        val replicantConfig = ReplicantConfig("127.0.0.1:2222", polishConfig, starburstConfig, "replicant")
        val replicantConnection = replicantConfig.connect(null)
        val success = replicantConnection.write("pass")
        assert(success)
        val serverBytes = replicantConnection.read(7)
        assert(serverBytes == "success".encodeToByteArray())
    }
}