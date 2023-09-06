package org.operatorfoundation.replicantandroid

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.operatorfoundation.keychainandroid.*
import org.junit.Test
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

        val customJson = Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
            allowStructuredMapKeys = true
            encodeDefaults = true
            serializersModule = SerializersModule {
                polymorphic(ToneBurst::class) {
                    subclass(Starburst::class, Starburst.serializer())
                }
            }
        }

        val replicantConfig = customJson.decodeFromString<ReplicantConfig>(configText)
        val replicantConnection = replicantConfig.connect(null)
        val success = replicantConnection.write("pass")
        assert(success)
        val serverBytes = replicantConnection.read(7)
        println("server bytes: ${serverBytes}")
    }

    @Test
    fun testReplicantDecode() {
        val configFile = File("")
        val configText = configFile.readText(Charsets.UTF_8)

        val customJson = Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
            allowStructuredMapKeys = true
            encodeDefaults = true
            serializersModule = SerializersModule {
                polymorphic(ToneBurst::class) {
                    subclass(Starburst::class, Starburst.serializer())
                }
            }
        }

        val replicantConfig = customJson.decodeFromString<ReplicantConfig>(configText)
        val replicantPublicKey = replicantConfig.polish.serverPublicKey
        when (replicantPublicKey) {
            is PublicKey.P256KeyAgreement -> println("replicant publicKey: ${PublicKey.javaPublicKeyToKeychainBytes(replicantPublicKey.javaPublicKey)[0]}")
            else -> {}
        }
    }
}