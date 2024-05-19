package ru.smak.qrcodereader.qr

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.set
import io.nayuki.qrcodegen.QrCode

class QrCreator {
    fun create(data: String): Bitmap? {
        if (data.isBlank()) return null
        val qr = QrCode.encodeText(data, QrCode.Ecc.QUARTILE)
        return toImage(qr, 5, 5)
    }

    private fun toImage(
        qr: QrCode,
        scale: Int,
        border: Int,
        lightColor: Int = Color.White.toArgb(),
        darkColor: Int = Color.Blue.toArgb(),
    ): Bitmap {
        require(!(scale <= 0 || border < 0)) { "Value out of range" }
        require(!(border > Int.MAX_VALUE / 2 || qr.size + border * 2L > Int.MAX_VALUE / scale)) { "Scale or border too large" }
        val result = Bitmap.createBitmap(
            (qr.size + border * 2) * scale,
            (qr.size + border * 2) * scale,
            Bitmap.Config.ARGB_8888,
        )
        for (y in 0 ..< result.height) {
            for (x in 0 ..< result.width) {
                val color = qr.getModule(x / scale - border, y / scale - border)
                result[x, y] = if (color) darkColor else lightColor
            }
        }
        return result
    }
}