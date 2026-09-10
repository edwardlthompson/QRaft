package org.qraft.data

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object BackupCrypto {
    private const val ITERATIONS = 10_000
    private const val KEY_BITS = 256
    private const val GCM_BITS = 128
    private const val SALT_LEN = 16
    private const val IV_LEN = 12

    fun wrap(plain: String, passphrase: String): String {
        if (passphrase.isBlank()) return plain
        val salt = ByteArray(SALT_LEN).also { SecureRandom().nextBytes(it) }
        val iv = ByteArray(IV_LEN).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key(passphrase, salt), GCMParameterSpec(GCM_BITS, iv))
        val packed = salt + iv + cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        return "QRAFT1:" + Base64.getEncoder().encodeToString(packed)
    }

    fun unwrap(blob: String, passphrase: String): String? {
        if (!blob.startsWith("QRAFT1:")) return blob
        if (passphrase.isBlank()) return null
        return runCatching {
            val packed = Base64.getDecoder().decode(blob.removePrefix("QRAFT1:"))
            val salt = packed.copyOfRange(0, SALT_LEN)
            val iv = packed.copyOfRange(SALT_LEN, SALT_LEN + IV_LEN)
            val body = packed.copyOfRange(SALT_LEN + IV_LEN, packed.size)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key(passphrase, salt), GCMParameterSpec(GCM_BITS, iv))
            String(cipher.doFinal(body), Charsets.UTF_8)
        }.getOrNull()
    }

    private fun key(passphrase: String, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(passphrase.toCharArray(), salt, ITERATIONS, KEY_BITS)
        val raw = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        return SecretKeySpec(raw, "AES")
    }
}
