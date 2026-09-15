package org.qraft.data

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object BackupCrypto {
    const val PREFIX_V1 = "QRAFT1:"
    const val PREFIX_V2 = "QRAFT2:"
    private const val ITER_V1 = 10_000
    private const val ITER_V2 = 600_000
    private const val KEY_BITS = 256
    private const val GCM_BITS = 128
    private const val SALT_LEN = 16
    private const val IV_LEN = 12

    fun wrap(plain: String, passphrase: String): String {
        if (passphrase.isBlank()) return plain
        return PREFIX_V1 + encrypt(plain, passphrase, ITER_V1)
    }

    fun wrapVault(plain: String, passphrase: String): String? {
        if (passphrase.isBlank()) return null
        return PREFIX_V2 + encrypt(plain, passphrase, ITER_V2)
    }

    fun unwrap(blob: String, passphrase: String): String? {
        val text = blob.trim()
        val (prefix, iterations) = when {
            text.startsWith(PREFIX_V2) -> PREFIX_V2 to ITER_V2
            text.startsWith(PREFIX_V1) -> PREFIX_V1 to ITER_V1
            else -> return text
        }
        if (passphrase.isBlank()) return null
        return decrypt(text.removePrefix(prefix), passphrase, iterations)
    }

    fun looksLikeBackup(blob: String): Boolean {
        val text = blob.trim()
        return text.startsWith(PREFIX_V2) || text.startsWith(PREFIX_V1) || text.startsWith("[")
    }

    private fun encrypt(plain: String, passphrase: String, iterations: Int): String {
        val salt = ByteArray(SALT_LEN).also { SecureRandom().nextBytes(it) }
        val iv = ByteArray(IV_LEN).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key(passphrase, salt, iterations), GCMParameterSpec(GCM_BITS, iv))
        val packed = salt + iv + cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(packed)
    }

    private fun decrypt(b64: String, passphrase: String, iterations: Int): String? = runCatching {
        val packed = Base64.getDecoder().decode(b64)
        val salt = packed.copyOfRange(0, SALT_LEN)
        val iv = packed.copyOfRange(SALT_LEN, SALT_LEN + IV_LEN)
        val body = packed.copyOfRange(SALT_LEN + IV_LEN, packed.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key(passphrase, salt, iterations), GCMParameterSpec(GCM_BITS, iv))
        String(cipher.doFinal(body), Charsets.UTF_8)
    }.getOrNull()

    private fun key(passphrase: String, salt: ByteArray, iterations: Int): SecretKeySpec {
        val spec = PBEKeySpec(passphrase.toCharArray(), salt, iterations, KEY_BITS)
        val raw = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        return SecretKeySpec(raw, "AES")
    }
}
