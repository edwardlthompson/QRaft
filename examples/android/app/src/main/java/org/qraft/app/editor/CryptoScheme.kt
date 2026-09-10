package org.qraft.app.editor

enum class CryptoScheme(val wire: String) {
    None(""),
    Bitcoin("bitcoin"),
    Ethereum("ethereum"),
    Litecoin("litecoin"),
    Dogecoin("dogecoin"),
    Monero("monero"),
    ;

    companion object {
        fun fromWire(value: String): CryptoScheme =
            entries.firstOrNull { it.wire.equals(value.trim(), ignoreCase = true) } ?: None
    }
}
