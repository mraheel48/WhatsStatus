package com.risetech.whatsstatus.utils

import android.os.Environment
import com.risetech.whatsstatus.dataModel.ItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.util.ArrayList


object Constants {

    const val REQUEST_CAPTURE_IMAGE = 10101
    const val REQUEST_GALLERY_IMAGE = 20202
    var fragmentVisible = 0

    var passList: ArrayList<ItemModel> = ArrayList()

    var itemPositionSelect = -1

    //Banner Ads
    val bannerId = "ca-app-pub-5448910982838601/8154040198"
    val bannerTestId = "ca-app-pub-3940256099942544/6300978111"

    //Interstitial Ads
    val interstitialId = "ca-app-pub-5448910982838601/5807912603"
    val interstitialTestId = "ca-app-pub-3940256099942544/1033173712"

    val filePathWhatApp =
        File(Environment.getExternalStorageDirectory().absoluteFile, "/WhatsApp/Media/.Statuses")
    val fileDownloadPath =
        File(Environment.getExternalStorageDirectory().absoluteFile, "/WhatsStatus/")

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