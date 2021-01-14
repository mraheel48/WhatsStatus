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

    var itemPositionSelect:Int = -1

    var itemPreviewPosition:Int = 0

    var fileStatus = false

    val license_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgZ3VzZPFiXoJ49Ttpn2nuhbrnvyacS3WEIxZ+3l7cJbkay9/bIP3XBprQUIy1UnV1l1JRy647pYlKyK2/UewX4RI5o7/KnsSPUlaIOfP4g1bb/mhPyFML3To7Qh/j7Khorg5R0YYEJV7tja8CIh+NRVxKZGJmgJe7kLT6Mt01AKGOb4Ez/UoKi2mE7erZ8LUnDhJafDHvDQNArK88fhHDZ4u4PeBF4jVeXYsWGRVIRub/iwfKy7byeuMn000UvrpW82GTD5YWSqgbuIaiCEeFBe6fcGqqR+teXi/loEcHfljtwxNS+b9QP+ueoyOP3megBmtLWmbakRzHcUXimJ/TwIDAQAB"
    val inapp_key = "status_saver_key"

    //Banner Ads
    val bannerId = "ca-app-pub-5448910982838601/8154040198"
    val bannerTestId = "ca-app-pub-3940256099942544/6300978111"

    //Interstitial Ads
    val interstitialId = "ca-app-pub-5448910982838601/5807912603"
    val interstitialTestId = "ca-app-pub-3940256099942544/1033173712"

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