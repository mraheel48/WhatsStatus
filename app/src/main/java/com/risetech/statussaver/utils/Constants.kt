package com.risetech.statussaver.utils

import android.os.Environment
import com.risetech.statussaver.dataModel.ItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.util.*


object Constants {

    const val REQUEST_CAPTURE_IMAGE = 10101
    const val REQUEST_GALLERY_IMAGE = 20202

    var fragmentVisible = 0

    var passList: ArrayList<ItemModel> = ArrayList()

    var itemPositionSelect: Int = -1

    var itemPreviewPosition: Int = 0

    var fileStatus = false

    //Banner Ads
    val bannerId = "ca-app-pub-5448910982838601/8154040198"
    val bannerTestId = "ca-app-pub-3940256099942544/6300978111"

    //Interstitial Ads
    val interstitialId = "ca-app-pub-5448910982838601/5807912603"
    val interstitialTestId = "ca-app-pub-3940256099942544/1033173712"

    val inAppKey = "status_saver"
    val inAppKeyTest = "android.test.purchased"
    //val base64Key  = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoYaoyKnm/TdymYfDhIRU+6vOVLC10dJtatzwjMWBF2g5poO09yEK315Ow7zJcur77rcdeW+1Tpu69Ym1GOKqkVTPBMKxt1KIUaR/4JTdbgtS1ANxYnO0pUrlnCD2PG+maHD1b837G6daBsBkea3SfjcOdiUU1g6tgShwFPXTcVaJCGPt5LVaNAGL0vrRxS/9AYmFb+PCkOLVU9sCvt8E1tbo7Wq0s+/qlzbgJtb6A1YYTcGaZSBQBuh+TNSei7+Oi7/FaDHbUdpxJecn1qGr2bhn85pCjRkt5yIzyBhvz0UDOvxmruXSP8qKduP5Jk00eqg8KlV/jVchBvbxWmTbrQIDAQAB"


    var inAppPrices: String = ""

    @Suppress("DEPRECATION")
    val filePathWhatApp =
        File(Environment.getExternalStorageDirectory().absoluteFile, "/WhatsApp/Media/.Statuses")

    @Suppress("DEPRECATION")
    val fileDownloadPath =
        File(Environment.getExternalStorageDirectory().absolutePath + "/StatusSaver/")

    val scopeIO: CoroutineScope = CoroutineScope(Dispatchers.IO)
    val scopeMain: CoroutineScope = CoroutineScope(Dispatchers.Main)
    val scopeDefault: CoroutineScope = CoroutineScope(Dispatchers.Default)
    val scopeUnconfined: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)
    val mainDispatcher = Dispatchers.Main

//    fun getAbsoluteDir(ctx: Context, optionalPath: String?): File? {
//        var rootPath: String
//        rootPath = if (optionalPath != null && optionalPath != "") {
//            ctx.getExternalFilesDir(optionalPath)!!.absolutePath
//        } else {
//            ctx.getExternalFilesDir(null)!!.absolutePath
//        }
//        // extraPortion is extra part of file path
//        val extraPortion = "Android/data/" + BuildConfig.APPLICATION_ID
//            .toString() + File.separator.toString() + "files" + File.separator
//        // Remove extraPortion
//        rootPath = rootPath.replace(extraPortion, "")
//
//        return File(rootPath)
//    }


}