package hu.levente.fazekas.receiptscanner.database.data

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant

class DateAdapter: ColumnAdapter<Instant, Long> {
    override fun encode(value: Instant) = value.epochSeconds
    override fun decode(databaseValue: Long) = Instant.fromEpochSeconds(databaseValue)
}