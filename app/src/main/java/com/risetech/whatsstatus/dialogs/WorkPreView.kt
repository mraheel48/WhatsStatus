package com.risetech.whatsstatus.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.risetech.whatsstatus.R
import com.risetech.whatsstatus.utils.Constants
import com.risetech.whatsstatus.utils.Utils
import java.io.File
import java.util.*


@Suppress("UNNECESSARY_SAFE_CALL")
class WorkPreView(
    private val activity: Activity,
    private val fileUri: File,
    callbackDownloadFile: DownloadFile
) {

    var callBackDownload = callbackDownloadFile
    var fileUriNew: Uri? = null
    lateinit var dialog: Dialog

    private fun setDialog() {

        //*****************************Create a custom Dialog***************************************//
        dialog = Dialog(activity, R.style.full_screen_dialog)
        val inflater = LayoutInflater.from(activity)
        @SuppressLint("InflateParams") val view =
            inflater.inflate(R.layout.work_preview, null, false)
        Objects.requireNonNull(dialog.window)
            ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        Objects.requireNonNull(dialog.window)
            ?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        dialog.window!!.statusBarColor = ContextCompat.getColor(activity, R.color.colorPrimaryDark)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) //before
        dialog.setContentView(view)
        dialog.setCancelable(false)

        val backBtn = view.findViewById<ImageView>(R.id.imageView261)

        val videoRoot = view.findViewById<FrameLayout>(R.id.video_root)
        val videoView = view.findViewById<VideoView>(R.id.video_view)
        val imageRoot = view.findViewById<ImageView>(R.id.imageView7)

        val btnShare = view.findViewById<ConstraintLayout>(R.id.share_icon)
        val btnDelete = view.findViewById<ConstraintLayout>(R.id.delete_icon)
        val btnRepost = view.findViewById<ConstraintLayout>(R.id.repost_icon)
        val btnDownload = view.findViewById<ConstraintLayout>(R.id.download_icon)

        if (Constants.fragmentVisible == 2) {
            btnDelete.visibility = View.VISIBLE
            btnDownload.visibility = View.GONE
        } else {
            btnDelete.visibility = View.GONE
            btnDownload.visibility = View.VISIBLE
        }

        val f = File(fileUri.toString())

        fileUriNew = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(activity, activity.packageName + ".fileprovider", f)
        } else {
            Uri.fromFile(f)
        }

        if (fileUri.toString().contains(".mp4")) {

            videoRoot.visibility = View.VISIBLE
            imageRoot.visibility = View.GONE
            videoView.setVideoURI(fileUriNew)
            videoView.start()

        } else {

            videoRoot.visibility = View.GONE
            imageRoot.visibility = View.VISIBLE

            Glide.with(activity)
                .load(fileUri)
                .into(imageRoot)
        }


        btnRepost.setOnClickListener {

            try {

                if (videoRoot.visibility == View.VISIBLE) {
                    videoView.stopPlayback()
                }

                //dialog.dismiss()

                shareVideoWhatsApp()

            } catch (e: Exception) {
                Utils.showToast(activity, "what app not install ")
            }
        }

        btnDelete.setOnClickListener {

            if (videoRoot.visibility == View.VISIBLE) {
                videoView.stopPlayback()
            }

            val fileDelete = File(fileUri.path)

            if (fileDelete.exists()) {

                val deleted: Boolean = fileDelete.delete()

                if (deleted) {
                    Toast.makeText(activity, " file is delete", Toast.LENGTH_SHORT).show()
                    callBackDownload.reFreshList()
                    dialog.dismiss()
                } else {
                    Toast.makeText(activity, "file not delete", Toast.LENGTH_SHORT).show()
                }
            }

        }

        btnShare.setOnClickListener {

            if (videoRoot.visibility == View.VISIBLE) {

                videoView.stopPlayback()

                try {

                    val intentShareFile = Intent(Intent.ACTION_SEND)
                    intentShareFile.type = "video/mp4"
                    intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUriNew)
                    activity.startActivity(
                        Intent.createChooser(
                            intentShareFile,
                            activity.resources.getString(R.string.share_file)
                        )
                    )


                    //dialog.dismiss()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {

                try {

                    val intentShareFile = Intent(Intent.ACTION_SEND)
                    intentShareFile.type = "image/jpeg"
                    intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUriNew)
                    activity.startActivity(
                        Intent.createChooser(
                            intentShareFile,
                            activity.resources.getString(R.string.share_file)
                        )
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }


        }

        btnDownload.setOnClickListener {

            if (videoRoot.visibility == View.VISIBLE) {
                videoView.stopPlayback()
            }

            callBackDownload.downloadPreFile(fileUri)
        }

        backBtn.setOnClickListener {

            if (videoRoot.visibility == View.VISIBLE) {
                videoView.stopPlayback()
            }

            dialog.dismiss()
        }

        dialog.show()

    }

    fun shareVideoWhatsApp() {
        // val uri = Uri.fromFile(v)
        val videoshare = Intent(Intent.ACTION_SEND)
        videoshare.type = "*/*"
        videoshare.setPackage("com.whatsapp")
        videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        videoshare.putExtra(Intent.EXTRA_STREAM, fileUriNew)
        activity.startActivity(videoshare)
    }

    fun disableDialog() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    init {
        setDialog()
    }

    interface DownloadFile {
        fun downloadPreFile(uri: File)
        fun reFreshList()
    }

}