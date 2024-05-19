package ru.smak.qrcodereader

import android.app.Application
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class QrCodeApp : Application() {
    private val moduleInstallClient by lazy {
        ModuleInstall.getClient(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()

        val qrScanner = GmsBarcodeScanning.getClient(applicationContext)
        val moduleInstallRequest = ModuleInstallRequest.newBuilder()
            .addApi(qrScanner)
            .build()

        moduleInstallClient
            .areModulesAvailable(qrScanner)
            .addOnSuccessListener {
                if (!it.areModulesAvailable()) {
                    moduleInstallClient.installModules(moduleInstallRequest)
                }
            }
    }
}