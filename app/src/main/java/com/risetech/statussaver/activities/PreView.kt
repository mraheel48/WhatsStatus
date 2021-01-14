package com.risetech.statussaver.activities

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.risetech.statussaver.R
import com.risetech.statussaver.dataModel.ItemModel
import com.risetech.statussaver.fragments.PreViewFragment
import com.risetech.statussaver.utils.Constants
import kotlinx.coroutines.*
import java.io.File
import kotlin.system.measureTimeMillis


class PreView : AppCompatActivity() {

    var passList: ArrayList<ItemModel> = ArrayList()

    lateinit var preView: PreViewFragment
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction

    lateinit var pagerViewRoot: ConstraintLayout

    var position: String? = null
    lateinit var btnShare: ConstraintLayout
    lateinit var btnRepost: ConstraintLayout
    lateinit var btnDownload: ConstraintLayout
    lateinit var btnDelete: ConstraintLayout
    lateinit var fileUriNew: Uri

    var fileCopyTime: Long = 500
    private lateinit var dialog: Dialog

    var deleted: Boolean = false

    lateinit var btnBack:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_view)

        if (intent.getStringExtra("itemPosition") != null) {
            position = intent.getStringExtra("itemPosition")
            Constants.itemPreviewPosition = position!!.toInt()
        }

        btnBack = findViewById(R.id.imageView261)
        pagerViewRoot = findViewById(R.id.pagerViewRoot)
        btnShare = findViewById(R.id.share_icon)
        btnRepost = findViewById(R.id.repost_icon)
        btnDownload = findViewById(R.id.download_icon)
        btnDelete = findViewById(R.id.delete_icon)

        //windows dialog code
        dialog = Dialog(this@PreView)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dilog_svg_loader)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        if (Constants.fragmentVisible == 2){
            btnDelete.visibility = View.VISIBLE
            btnDownload.visibility = View.GONE
        }

        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()

        preView = PreViewFragment()

        fragmentTransaction
            .replace(R.id.pagerViewRoot, preView)
            .commit()

        btnShare.setOnClickListener {

            val f = File(Constants.passList[Constants.itemPreviewPosition].text)

            fileUriNew = if (Build.VERSION.SDK_INT >= 24) {
                FileProvider.getUriForFile(
                    this@PreView,
                    this@PreView.packageName + ".fileprovider",
                    f
                )
            } else {
                Uri.fromFile(f)
            }

            shareFile()


        }

        btnRepost.setOnClickListener {

            val f = File(Constants.passList[Constants.itemPreviewPosition].text)
            fileUriNew = if (Build.VERSION.SDK_INT >= 24) {
                FileProvider.getUriForFile(
                    this@PreView,
                    this@PreView.packageName + ".fileprovider",
                    f
                )
            } else {
                Uri.fromFile(f)
            }

            rePostWhatsApp()
        }

        btnDownload.setOnClickListener {

            val f = File(Constants.passList[Constants.itemPreviewPosition].text)

            fileUriNew = if (Build.VERSION.SDK_INT >= 24) {
                FileProvider.getUriForFile(
                    this@PreView,
                    this@PreView.packageName + ".fileprovider",
                    f
                )
            } else {
                Uri.fromFile(f)
            }

            downloadPreFile(f)
        }

        btnDelete.setOnClickListener {

            val fileDelete = File(Constants.passList[Constants.itemPreviewPosition].text)

            if (fileDelete.exists()) {
                deleted = fileDelete.delete()
            }

            dialog.show()

            if (dialog.isShowing) {

                object : CountDownTimer(500, 1) {
                    override fun onTick(l: Long) {
                    }

                    override fun onFinish() {

                        if (dialog.isShowing) {
                            dialog.dismiss()
                        }

                        if (deleted) {
                            Toast.makeText(this@PreView, " file is delete", Toast.LENGTH_SHORT)
                                .show()
                            Constants.fileStatus = true
                            finish()
                        } else {
                            Toast.makeText(this@PreView, "file not delete", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }

                }.start()

            }


        }

        btnBack.setOnClickListener {
            finish()
        }

    }

    fun rePostWhatsApp() {
        try {
            val videoshare = Intent(Intent.ACTION_SEND)
            videoshare.type = "*/*"
            videoshare.setPackage("com.whatsapp")
            videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            videoshare.putExtra(Intent.EXTRA_STREAM, fileUriNew)
            startActivity(videoshare)
        } catch (e: Exception) {
            e.printStackTrace()

            val intentShareFile = Intent(Intent.ACTION_SEND)
            intentShareFile.type = "*/*"
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUriNew)
            this.startActivity(
                Intent.createChooser(
                    intentShareFile,
                    this.resources.getString(R.string.share_file)
                )
            )
        }
    }

    fun shareFile() {
        val intentShareFile = Intent(Intent.ACTION_SEND)
        intentShareFile.type = "*/*"
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUriNew)
        this.startActivity(
            Intent.createChooser(
                intentShareFile,
                this.resources.getString(R.string.share_file)
            )
        )
    }

    lateinit var job: Deferred<Boolean>

    fun downloadPreFile(uri: File) {

        lifecycleScope.launch {

            Constants.scopeIO.launch {

                val executionTimeOut = measureTimeMillis {

                    job = async {
                        downloadFile(uri)
                    }

                    job.await()

                }

                fileCopyTime += executionTimeOut
                updateUiProgress()

            }
        }

    }

    suspend fun downloadFile(fileUri: File): Boolean {

        File(fileUri.toString()).copyTo(
            File(
                Constants.fileDownloadPath.toString() + fileUri.toString()
                    .replace(Constants.filePathWhatApp.toString(), "")
            ), true
        )

        val filePath = Constants.fileDownloadPath.toString() + fileUri.toString()
            .replace(Constants.filePathWhatApp.toString(), "")

        MediaScannerConnection.scanFile(
            this@PreView,
            arrayOf(filePath),
            arrayOf("image/jpeg/video/mp4"),
            null
        )
        return true
    }

    fun addVideo(videoFile: File): Uri? {
        val values = ContentValues(3)
        values.put(MediaStore.Video.Media.TITLE, "My video title")
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Video.Media.DATA, videoFile.absolutePath)
        return contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
    }

    suspend fun updateUiProgress() {
        withContext(Dispatchers.Main) {
            showProgressDialog(fileCopyTime)
        }
    }

    suspend fun showProgressDialog(timeStay: Long) {

        withContext(Constants.mainDispatcher) {

            dialog.show()

            if (dialog.isShowing) {

                object : CountDownTimer(timeStay, 1) {
                    override fun onTick(l: Long) {
                    }

                    override fun onFinish() {
                        dialog.dismiss()
                        fileCopyTime = 500
                       // Utils.showToast(this@PreView, "file is download")
                        Constants.fileStatus = true
                    }

                }.start()

            }

        }
    }

}