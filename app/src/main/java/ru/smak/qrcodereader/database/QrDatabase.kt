package ru.smak.qrcodereader.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [QrInfo::class], version = 1, exportSchema = false)
abstract class QrDatabase : RoomDatabase(){
    abstract fun getQrDao(): QrDao

}