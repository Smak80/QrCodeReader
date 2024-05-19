package ru.smak.qrcodereader.qr

import android.content.Context
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class QrScanner {

    private var _qrScanner: GmsBarcodeScanner? = null

    private fun getScanner(context: Context) = _qrScanner ?: run{
        GmsBarcodeScanning.getClient(context).also {
            _qrScanner = it
        }
    }

    fun scanQr(context: Context, onLoad: (String)->Unit) {
        getScanner(context)
            .startScan()
            .addOnSuccessListener {
                onLoad(it.rawValue ?: "")
            }
            .addOnFailureListener{
                onLoad("")
            }
    }
}