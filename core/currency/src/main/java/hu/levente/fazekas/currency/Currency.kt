package hu.levente.fazekas.currency

enum class Currency(val symbol: String, val fullName: String, val icon: Int) {
    HUF("FT", "Hungarian Forint", R.drawable.flag_huf),
    EUR("€", "Euro", R.drawable.flag_eur),
    USD("$", "US Dollar", R.drawable.flag_usd),
}