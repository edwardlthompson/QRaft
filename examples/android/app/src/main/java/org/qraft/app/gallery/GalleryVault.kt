package org.qraft.app.gallery

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.qraft.app.R
import org.qraft.data.BackupCrypto
import org.qraft.data.DataStoreProfileRepository

object GalleryVault {
    @Volatile
    private var pendingPassphrase: String = ""

    fun holdPassphrase(value: String) {
        pendingPassphrase = value
    }

    fun takePassphrase(): String {
        val value = pendingPassphrase
        pendingPassphrase = ""
        return value
    }

    suspend fun save(
        context: Context,
        repo: DataStoreProfileRepository,
        uri: Uri,
        passphrase: String,
    ): Int {
        val blob = withContext(Dispatchers.Default) {
            BackupCrypto.wrapVault(repo.exportJson(), passphrase)
        } ?: return R.string.profiles_vault_need_passphrase
        val ok = withContext(Dispatchers.IO) {
            VaultFile.write(context.contentResolver, uri, blob)
        }
        return if (ok) R.string.profiles_vault_saved else R.string.profiles_vault_write_failed
    }

    suspend fun open(
        context: Context,
        repo: DataStoreProfileRepository,
        uri: Uri,
        passphrase: String,
    ): Int {
        val blob = withContext(Dispatchers.IO) {
            VaultFile.read(context.contentResolver, uri)
        } ?: return R.string.profiles_vault_read_failed
        return importBlob(context, repo, blob, passphrase)
    }

    suspend fun restoreClipboard(
        context: Context,
        repo: DataStoreProfileRepository,
        passphrase: String,
    ): Int {
        val text = (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            .primaryClip?.getItemAt(0)?.coerceToText(context)?.toString().orEmpty()
        return importBlob(context, repo, text, passphrase)
    }

    private suspend fun importBlob(
        context: Context,
        repo: DataStoreProfileRepository,
        blob: String,
        passphrase: String,
    ): Int {
        if (!BackupCrypto.looksLikeBackup(blob)) return R.string.profiles_vault_invalid
        val json = withContext(Dispatchers.Default) { BackupCrypto.unwrap(blob, passphrase) }
            ?: return R.string.profiles_vault_bad_passphrase
        return if (GalleryRestore.applyJson(context, repo, json)) {
            R.string.profiles_restored
        } else {
            R.string.profiles_vault_empty
        }
    }
}
