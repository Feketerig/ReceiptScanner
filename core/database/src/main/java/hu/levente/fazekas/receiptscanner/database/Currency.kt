package hu.levente.fazekas.receiptscanner.database

enum class Currency(val symbol: String) {
    HUF("FT"),
    EUR("€"),
    USD("$"),
}