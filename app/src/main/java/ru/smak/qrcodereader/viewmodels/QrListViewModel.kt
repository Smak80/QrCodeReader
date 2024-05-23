package ru.smak.qrcodereader.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import ru.smak.qrcodereader.QrCodeApp
import ru.smak.qrcodereader.qr.QrCreator
import ru.smak.qrcodereader.qr.QrScanner

class QrListViewModel(app: Application) : AndroidViewModel(app) {

    private val scanner = QrScanner()
    private val qrCreator: QrCreator = QrCreator()
    private val _texts = mutableStateListOf<String>()
    val texts: List<String>
        get() = _texts.asReversed()

    fun scanQr(){
        scanner.scanQr(getApplication<QrCodeApp>().applicationContext){
            addQrText(it)
        }
    }

    fun addQrText(text: String){
        if (text.isNotBlank()){
            _texts.add(text)
        }
    }

    fun createQr(text: String): ImageBitmap? =
        qrCreator.create(text)?.asImageBitmap()

}