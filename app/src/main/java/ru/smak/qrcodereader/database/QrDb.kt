package ru.smak.qrcodereader.database

import android.content.Context
import androidx.room.Room

object QrDb {
    private lateinit var db: QrDatabase

    operator fun get(context: Context): QrDatabase {
        if (!::db.isInitialized) {
            db = Room.databaseBuilder(
                context = context,
                QrDatabase::class.java,
                "QR_DB"
            ).build()
        }
        return db
    }
}