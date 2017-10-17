package toolsutilkt

import android.content.Context
import android.security.KeyPairGeneratorSpec
import android.text.TextUtils
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal

/**
 * Created by xiejinpeng on 2017/10/16.
 */

private val keyStore: KeyStore? by lazy { initKeyStore() }

private fun initKeyStore(): KeyStore? {
    try {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore
    } catch (e: Exception) {
        when (e) {
            is IOException, is NoSuchAlgorithmException, is KeyStoreException, is CertificateException -> {
                e.printStackTrace()
            }
        }
    }
    return null
}

fun getKeyAliases(): List<String> {
    if (keyStore == null)
        return listOf()

    val keyAliases = arrayListOf<String>()
    try {
        val aliases = keyStore!!.aliases()
        while (aliases.hasMoreElements()) {
            keyAliases.add(aliases.nextElement())
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return keyAliases
}

fun isAliasExist(alias: String): Boolean {
    return getKeyAliases()
            .any { TextUtils.equals(it, alias) }
}

fun generateNewKeys(context: Context, alias: String) {
    if (keyStore == null)
        return

    try {

        if (!keyStore!!.containsAlias(alias)) {
            val start = Calendar.getInstance()
            val end = Calendar.getInstance()
            end.add(Calendar.YEAR, 1)
            val spec = KeyPairGeneratorSpec.Builder(context)
                    .setAlias(alias)
                    .setSubject(X500Principal("CN=CN, O=Android Authority , C=COMPANY"))
                    .setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build()

            val generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore")
            generator.initialize(spec)
            generator.generateKeyPair()
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun deleteKey(alias: String) {
    try {
        keyStore?.deleteEntry(alias)
    } catch (e: KeyStoreException) {
        e.printStackTrace()
    }
}

fun encryptString(encryptStr: String, alias: String): String {
    if (keyStore == null || TextUtils.isEmpty(encryptStr))
        return encryptStr

    try {
        val privateKeyEntry = keyStore!!.getEntry(alias, null) as KeyStore.PrivateKeyEntry
        val publicKey = privateKeyEntry.certificate.publicKey as RSAPublicKey

        val input = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        input.init(Cipher.ENCRYPT_MODE, publicKey)

        val outputStream = ByteArrayOutputStream()
        val cipherOutputStream = CipherOutputStream(outputStream, input)
        cipherOutputStream.write(encryptStr.toByteArray(Charsets.UTF_8))
        cipherOutputStream.close()

        val bytes = outputStream.toByteArray()

        val encryptedStr = Base64.encodeToString(bytes, Base64.DEFAULT)
        return encryptedStr

    } catch (e: Exception) {
        e.printStackTrace()
    }

    return encryptStr
}

fun decryptString(cipherStr: String, alias: String): String {
    if (keyStore == null)
        return ""
    try {
        val privateKeyEntry = keyStore!!.getEntry(alias, null) as KeyStore.PrivateKeyEntry

        val output = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)

        val cipherInputStream = CipherInputStream(
                ByteArrayInputStream(Base64.decode(cipherStr, Base64.DEFAULT)), output)

        val values = arrayListOf<Byte>()
        do {
            val nextByte = cipherInputStream.read()
            values.add(nextByte.toByte())
        } while (nextByte != -1)

        if (values[values.size - 1].toInt() == -1)
            values.removeAt(values.size - 1)

        val bytes = values.toByteArray()
        val decrypted = String(bytes, 0, bytes.size, Charsets.UTF_8)
        return decrypted

    } catch (e: Exception) {
        e.printStackTrace()
    }
    return cipherStr
}




