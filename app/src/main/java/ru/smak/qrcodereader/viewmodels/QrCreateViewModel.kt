package ru.smak.qrcodereader.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import ru.smak.qrcodereader.qr.QrCreator

class QrCreateViewModel : ViewModel() {

    private val qrCreator: QrCreator = QrCreator()

    var qrText by mutableStateOf("")
    var qrImage: ImageBitmap? by mutableStateOf(null)

    fun init(){
        qrText = ""
    }

    fun updateText(value: String) {
        qrText = value
        qrImage = qrCreator.create(qrText)?.asImageBitmap()
    }
}