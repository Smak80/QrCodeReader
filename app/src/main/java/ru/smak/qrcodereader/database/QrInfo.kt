package ru.smak.qrcodereader.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_info")
data class QrInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String = ""
)