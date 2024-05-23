package ru.smak.qrcodereader.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QrDao {
    @Insert(entity = QrInfo::class)
    suspend fun addQrDao(inf: QrInfo): Long

    @Query("SELECT * FROM `qr_info`")
    fun loadInfo(): Flow<List<QrInfo>>
}