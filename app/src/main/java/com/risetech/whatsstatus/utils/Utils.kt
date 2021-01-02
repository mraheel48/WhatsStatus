package com.risetech.whatsstatus.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import com.risetech.whatsstatus.R
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    fun fileDownloadPath(activity: Activity): File {

        val aDirArray = ContextCompat.getExternalFilesDirs(activity, null)

        // val aExtDcimDir = File(aDirArray[0], Environment.DIRECTORY_DCIM)

        val newFolder = File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString())

        if (!newFolder.exists()) {
            newFolder.mkdir()
        }

        // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

        //val root = mContext.getExternalFilesDir(null)!!.absolutePath
        //val fileDownloadPath = File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "/WhatsStatus/")
        // val fileDownloadPath = File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "/")
       /* File(
            activity.getExternalFilesDir(Environment.DIRECTORY_DCIM),
            "/WhatsStatus"
        )*/
        return newFolder
    }

    fun showToast(context: Context, message: String) {
        val activity = context as Activity
        activity.runOnUiThread { //show your Toast here..
            //Toast.makeText(context, "Connection Successful", Toast.LENGTH_LONG).show()
            Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun getRealPathFromURI(uri: Uri?, context: Context): String? {
        if (uri == null) {
            return null
        }
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }
        return uri.path
    }

    //**********************Method for checking network************************************//
    fun isNetworkAvailable(context: Context?): Boolean {
        val connectivityManager =
            (context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    //*************************************************************************************//

    //**************************Method for saving image file (.png)*********************//
    //Need to Write permission
    fun saveImageThumbnail(view: View?, folderName: String, subFolder: String, context: Context) {

        view?.let {

            //val bitmap = createBitmapFromView(view)
            val bitmap = view.drawToBitmap(Bitmap.Config.ARGB_8888)

            val mFileName = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(System.currentTimeMillis())
            val file = Environment.getExternalStorageDirectory()
            val path = File(file.absolutePath + "/" + folderName + "/MyWork/" + subFolder + "/img")
            path.mkdirs()
            val fileName = "img$mFileName.png"
            val newFile = File(path, fileName)
            try {
                val out = FileOutputStream(newFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
                out.close()
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(newFile.path),
                    arrayOf("image/jpeg"),
                    null
                )

                /* if (newFile.exists()) {
                    savePDFFinal(view, folderName, labelName, mFileName);
                }*/

            } catch (e: Exception) {
                e.printStackTrace()
            }

            Toast.makeText(context, "File saved in$newFile", Toast.LENGTH_SHORT).show()
        }

    }

    //***************************************************************************************************//

    //**************************Method for saving image file (.png)*********************//
    //Need to Write permission
    fun saveBitmapImageThumbnail(
        bitmap: Bitmap?,
        folderName: String,
        subFolder: String,
        context: Context
    ) {

        bitmap?.let {

            //val bitmap = createBitmapFromView(view)
            //val bitmap = view.drawToBitmap(Bitmap.Config.ARGB_8888)

            val mFileName = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(System.currentTimeMillis())
            val file = Environment.getExternalStorageDirectory()
            val path = File(file.absolutePath + "/" + folderName + "/MyWork/" + subFolder + "/img")
            path.mkdirs()
            val fileName = "img$mFileName.png"
            val newFile = File(path, fileName)
            try {
                val out = FileOutputStream(newFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
                out.close()
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(newFile.path),
                    arrayOf("image/jpeg"),
                    null
                )

                /* if (newFile.exists()) {
                    savePDFFinal(view, folderName, labelName, mFileName);
                }*/

            } catch (e: Exception) {
                e.printStackTrace()
            }

            Toast.makeText(context, "File saved in$newFile", Toast.LENGTH_SHORT).show()
        }

    }


    //***************************************************************************************************//
    //**************************Method read byte for a file path*********************//
    fun getByte(path: String): ByteArray {

        var getBytes = byteArrayOf()

        try {

            val file = File(path)
            getBytes = ByteArray(file.length().toInt())
            val `is`: InputStream = FileInputStream(file)
            `is`.read(getBytes)
            `is`.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return getBytes
    }


    fun getResizedBitmap(bm: Bitmap, newHeight: Int, newWidth: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // create a matrix for the manipulation
        val matrix = Matrix()
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight)
        // recreate the new Bitmap
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
    }

    //**************************Method for saving image file (.pdf)****************************************//
    //Need to Write permission
    fun savePdfFile(view: View?, folderName: String, subFolder: String, context: Context) {

        view?.let {

            try {

                //File Name as a current time Name
                val mFileName = SimpleDateFormat(
                    "yyyyMMdd_HHmmss",
                    Locale.getDefault()
                ).format(System.currentTimeMillis())
                val file = Environment.getExternalStorageDirectory()
                val path =
                    File(file.absolutePath + "/" + folderName + "/MyWork/" + subFolder + "/pdf")
                path.mkdirs()
                val newFile = File(path, "pdf$mFileName.pdf")
                val document = PdfDocument()
                val pageInfo = PageInfo.Builder(view.width, view.height, 1).create()
                val page = document.startPage(pageInfo)

                //View content = view;
                view.draw(page.canvas)
                view.draw(page.canvas)
                // finish the page
                document.finishPage(page)

                document.writeTo(FileOutputStream(newFile))
                if (newFile.exists()) {
                    //Toast.makeText(Application.context, "file is found", Toast.LENGTH_SHORT).show();
                    saveImagePdfThumbnail(view, folderName, subFolder, mFileName, context)
                }

                document.close()
                Toast.makeText(context, "File saved in$newFile", Toast.LENGTH_SHORT).show()

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }

    }
    //***************************************************************************************************//

    //**************************Method for saving image file (.png)*********************//
    //Need to Write permission
    fun saveImagePdfThumbnail(
        view: View?,
        folderName: String,
        subFolder: String,
        mFileName: String,
        context: Context
    ) {

        view?.let {

            try {

                //val bitmap = createBitmapFromView(view)
                val bitmap = view.drawToBitmap(Bitmap.Config.ARGB_8888)
                /* String mFileName = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(System.currentTimeMillis());*/
                val file = Environment.getExternalStorageDirectory()
                val path =
                    File(file.absolutePath + "/" + folderName + "/MyWork/" + subFolder + "/img")
                path.mkdirs()
                val fileName = "pdf$mFileName.png"
                val newFile = File(path, fileName)

                val out = FileOutputStream(newFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
                out.close()
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(newFile.path),
                    arrayOf("image/jpeg"),
                    null
                )

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }


        }

        //Toast.makeText(Application.context, "File saved in" + newFile, Toast.LENGTH_SHORT).show();
    }

    //***************************************************************************************************//

    //**************************Method for saving image file (.pdf)************************************//
    //Need to Write permission
    fun savePDFFinal(
        context: Context,
        view: View,
        folderName: String,
        labelName: String,
        fileName: String
    ) {
        val file = Environment.getExternalStorageDirectory()
        val newFile: File
        val path: File
        path = File(file.absolutePath + "/" + folderName + "/MyWork/assets/" + labelName)
        val newFileName = "$labelName$fileName.pdf"
        newFile = File(path, newFileName)
        path.mkdirs()
        val document = PdfDocument()
        val pageInfo = PageInfo.Builder(view.width, view.height, 1).create()
        val page = document.startPage(pageInfo)

        //View content = view;
        view.draw(page.canvas)
        view.draw(page.canvas)
        // finish the page
        document.finishPage(page)
        try {
            document.writeTo(FileOutputStream(newFile))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        document.close()
        Toast.makeText(context, "File saved in$path", Toast.LENGTH_SHORT).show()
    }

    //***************************************************************************************************//
    var fileuri: Uri? = null

    //*********************************Method for shear pdf***************************************//
    fun sharePdfFile(
        view: View?,
        folderName: String = "",
        subFolder: String = "",
        context: Context
    ) {

        view?.let {

            try {

                //File Name as a current time Name
                val mFileName = SimpleDateFormat(
                    "yyyyMMdd_HHmmss",
                    Locale.getDefault()
                ).format(System.currentTimeMillis())
                val file = Environment.getExternalStorageDirectory()
                val path = File(file.absolutePath + "/" + folderName + "/" + subFolder + "/share")
                path.mkdirs()
                val newFile = File(path, "PDF$mFileName.pdf")
                val document = PdfDocument()
                val pageInfo = PageInfo.Builder(view.width, view.height, 1).create()
                val page = document.startPage(pageInfo)
                val content: View
                content = view
                content.draw(page.canvas)
                view.draw(page.canvas)

                // finish the page
                document.finishPage(page)
                document.writeTo(FileOutputStream(newFile))
                if (newFile.exists()) {
                    fileuri = if (Build.VERSION.SDK_INT >= 24) {
                        FileProvider.getUriForFile(
                            context,
                            view.context.resources.getString(R.string.file_provider),
                            newFile
                        )
                    } else {
                        Uri.fromFile(newFile)
                    }
                    val intentShareFile = Intent(Intent.ACTION_SEND)
                    intentShareFile.type = "application/pdf"
                    intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, fileuri)
                    context.startActivity(
                        Intent.createChooser(
                            intentShareFile,
                            view.context.resources.getString(R.string.share_file)
                        )
                    )
                }

                document.close()

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }
    }

    //******************************************************************************************//
    //*************************Convert any view into Bitmap*************************************//
    fun createBitmapFromView(v: View): Bitmap {

        val bitmap = Bitmap.createBitmap(
            v.measuredWidth,
            v.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(bitmap)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)

        return bitmap

    }

    //*********************************************************************************************//
    //**************************This method hide keyboard*******************************************//
    fun hideKeyboardFromView(context: Context, view: View) {
        val imm = (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /*fun hideKeyBoard(context: Context?) {
        context?.let {
            val imm = context.getSystemService(
                    Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0)
        }
    }*/

    fun showKeyBoard(context: Context?) {

        context?.let {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    //**********************************************************************************************//
    //**************************This method pick image form gallery********************************//
    //Need to Write permission
    //onActivityResult Code
    //     if (requestCode == Constants.REQUEST_GELLERY_IMAGE && resultCode == RESULT_OK && null != data) {
    //
    //        Uri selectedImage = data.getData();
    //        String[] filePathColumn = {MediaStore.Images.Media.DATA};
    //        Cursor cursor = getContentResolver().query(selectedImage,
    //                filePathColumn, null, null, null);
    //        cursor.moveToFirst();
    //        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
    //
    //        String picturePath = cursor.getString(columnIndex);
    //        cursor.close();
    //
    //        setImageFormGellery(picturePath);
    //
    //    }
    fun pickGalleryImage(activity: Activity) {

        val pictureIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity.startActivityForResult(
            pictureIntent,
            Constants.REQUEST_GALLERY_IMAGE
        )

    }

    //**********************************************************************************************//
    //***************************This method pick image form Camera*********************************//
    //Need to Camera permission
    //onActivityResult Code
    /* if (requestCode == Constants.REQUEST_CAPTURE_IMAGE &&
    resultCode == RESULT_OK) {

        if (data != null && data.getExtras() != null) {

            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            if (imageBitmap != null)
                addImage(imageBitmap);

        }
    }*/
    fun pickCameraImage(activity: Activity) {
        val pictureIntent = Intent(
            MediaStore.ACTION_IMAGE_CAPTURE
        )
        if (pictureIntent.resolveActivity(activity.packageManager) != null) {
            activity.startActivityForResult(
                pictureIntent,
                Constants.REQUEST_CAPTURE_IMAGE
            )
        }
    }

    //**********************************************************************************************//
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            context.resources.displayMetrics
        ).toInt()
    }

}