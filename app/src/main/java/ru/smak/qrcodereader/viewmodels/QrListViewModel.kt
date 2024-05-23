package ru.smak.qrcodereader.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.smak.qrcodereader.QrCodeApp
import ru.smak.qrcodereader.database.QrDb
import ru.smak.qrcodereader.database.QrInfo
import ru.smak.qrcodereader.qr.QrCreator
import ru.smak.qrcodereader.qr.QrScanner

class QrListViewModel(app: Application) : AndroidViewModel(app) {

    private val scanner = QrScanner()
    private val qrCreator: QrCreator = QrCreator()
    private val _texts = mutableStateListOf<QrInfo>()
    val texts: List<QrInfo>
        get() = _texts.asReversed()

    private val qrDao = QrDb[getApplication<Application>().applicationContext].getQrDao()

    init{
        loadData()
    }

    fun scanQr(){
        scanner.scanQr(getApplication<QrCodeApp>().applicationContext){
            addQrText(it)
        }
    }

    private fun loadData(){
        viewModelScope.launch(Dispatchers.IO) {
            qrDao.loadInfo().collect {
                withContext(Dispatchers.Main){
                    _texts.clear()
                    _texts.addAll(it)
                }
            }
        }
    }

    fun addQrText(text: String){
        if (text.isNotBlank()){
            val qr = QrInfo(text = text)
            _texts.add(qr)
            viewModelScope.launch(Dispatchers.IO) {
                qrDao.addQrDao(qr)
            }
        }
    }


    fun createQr(text: String): ImageBitmap? =
        qrCreator.create(text)?.asImageBitmap()

}