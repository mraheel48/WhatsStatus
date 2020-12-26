package com.risetech.whatsstatus.activities


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.risetech.whatsstatus.R
import com.risetech.whatsstatus.dataModel.ItemModel
import com.risetech.whatsstatus.dialogs.*
import com.risetech.whatsstatus.fragments.CategoryFragment
import com.risetech.whatsstatus.fragments.HomeFragment
import com.risetech.whatsstatus.utils.Constants
import com.risetech.whatsstatus.utils.FeedbackUtils
import com.risetech.whatsstatus.utils.Utils
import com.risetech.whatsstatus.viewPagerAdapter.MySaveWorkAdapter
import com.risetech.whatsstatus.viewPagerAdapter.MyWorkAdapter
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.coroutines.*
import org.apache.commons.io.comparator.LastModifiedFileComparator
import java.io.*
import java.util.*
import kotlin.system.measureTimeMillis


@Suppress("UNNECESSARY_SAFE_CALL")
class MainActivity : AppCompatActivity(), ProDialog.BuyClick, MyWorkAdapter.ItemClick,
    MySaveWorkAdapter.ItemClick, WorkPreView.DownloadFile {

    lateinit var navBtn: ImageView
    lateinit var drawer: DrawerLayout
    lateinit var homeRoot: ConstraintLayout
    lateinit var permissionRoot: ConstraintLayout

    //nav_Root
    lateinit var slidingRootNav: SlidingRootNav
    lateinit var navProBtn: ConstraintLayout
    lateinit var navRateUs: ConstraintLayout
    lateinit var navContactUs: ConstraintLayout
    lateinit var navShareApp: ConstraintLayout
    lateinit var navGuideApp: ConstraintLayout
    lateinit var navPrivacyPolicy: ConstraintLayout

    lateinit var homeF: HomeFragment
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction

    lateinit var btnRefreshPer: View

    var pathListSelect: ArrayList<ItemModel> = ArrayList()

    lateinit var downloadBtn: ImageView
    lateinit var reFreshData: ImageView

    private val scopeIO: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private lateinit var customDialogBuilder: AlertDialog.Builder
    private lateinit var customDialog: AlertDialog

    val TAG = "MainActivity"

    var imgPathWhatApp: ArrayList<ItemModel> = ArrayList()
    var videosPathWhatApp: ArrayList<ItemModel> = ArrayList()
    var savedPathWhatApp: ArrayList<ItemModel> = ArrayList()

    private lateinit var dialog: Dialog
    var fileCount = 0
    var selectionCount = 0
    var fileCopyTime: Long = 1000

    var listFile: Array<File>? = null
    var saveListFile: Array<File>? = null
    var myWorkAdapter: MyWorkAdapter? = null

    var categoryFragment: CategoryFragment = CategoryFragment()

    lateinit var localDownloadPath: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init Id Layout
        navBtn = findViewById(R.id.nav_btn)
        drawer = findViewById(R.id.drawer_layout)
        homeRoot = findViewById(R.id.fragmentRoot)
        btnRefreshPer = findViewById(R.id.btnRefresh)
        permissionRoot = findViewById(R.id.permissionRoot)

        downloadBtn = findViewById(R.id.nav_btn2)
        reFreshData = findViewById(R.id.refresh_data)

        //windows dialog code
        dialog = Dialog(this@MainActivity)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dilog_svg_loader)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)

        localDownloadPath = Utils.fileDownloadPath(this@MainActivity)
        Log.e(TAG,"${localDownloadPath}")

        //set nav_Draw
        navDrawMethod()

        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()


        /*try {
         navDrawMethod()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }*/

        navBtn.setOnClickListener { openCloseNavigationView() }

        btnRefreshPer.setOnClickListener { openHomeFragment() }

        downloadBtn.setOnClickListener {
            downloadBtn.isClickable = false
            copyFileBG()
            //File(pathListSelect[0].text.toString()).copyTo(File(Constants.fileDownloadPath.toString() + pathListSelect[0].text.toString().replace(Constants.filePathWhatApp.toString(), "")), false)
        }

        reFreshData.setOnClickListener {
            reFreshData.isClickable = false
            updateFragmentUI()
        }

        openHomeFragment()

    }

    private fun copyFileBG() {

        lifecycleScope.launch {

            Constants.scopeIO.launch {

                val executionTimeOut = measureTimeMillis {

                    val job1: Deferred<Int> = async {
                        copyFileResult(fileCount)
                    }

                    fileCount = job1.await()

                    if (fileCount < pathListSelect.size) {
                        copyFileBG()
                    } else {
                        updateUiProgress(fileCopyTime)
                        //Log.e(TAG, "else ${fileCopyTime
                    }

                }

                fileCopyTime += executionTimeOut

                Log.e(TAG, "This is a time Job complete $fileCopyTime ms..")


            }
        }
    }

    suspend fun updateUiProgress(timeStay: Long) {

        withContext(Dispatchers.Main) {
            updateFragmentUI()
        }

    }

    suspend fun updateHomeFragment(timeStay: Long) {

        withContext(Constants.mainDispatcher) {

            dialog.show()

            if (dialog.isShowing) {

                object : CountDownTimer(timeStay, 1) {
                    override fun onTick(l: Long) {
                    }

                    override fun onFinish() {
                        Log.e(TAG, "dialog dismiss")
                        dialog.dismiss()

                        homeF = HomeFragment()
                        fragmentTransaction
                            .replace(R.id.fragmentRoot, homeF)
                            .commit()

                        homeRoot.visibility = View.VISIBLE
                        permissionRoot.visibility = View.GONE

                    }

                }.start()

            }

        }
    }

    private fun updateFragmentUI() {

        pathListSelect.clear()
        fileCount = 1500
        fileCount = 0

        readLocalEndData(false)

        if (pathListSelect.isNotEmpty()) {
            downloadBtn.visibility = View.VISIBLE
            reFreshData.visibility = View.GONE
        } else {
            downloadBtn.visibility = View.GONE
            reFreshData.visibility = View.VISIBLE
        }

        downloadBtn.isClickable = true
        reFreshData.isClickable = true
    }

    suspend fun copyFileResult(fileNumber: Int): Int {
        File(pathListSelect[fileNumber].text).copyTo(

            File(
                localDownloadPath.toString() + pathListSelect[fileNumber].text
                    .replace(Constants.filePathWhatApp.toString(), "")
            ), true
        )

        return fileNumber + 1
    }

    suspend fun downloadFile(fileUri: File): Boolean {

        File(File(fileUri.path).toString()).copyTo(

            File(
                localDownloadPath.toString() + fileUri.path.toString()
                    .replace(Constants.filePathWhatApp.toString(), "")
            ), true
        )

        return true
    }

    suspend fun readWhatAppData(): Boolean {

        imgPathWhatApp.clear()
        videosPathWhatApp.clear()
        savedPathWhatApp.clear()

        val whatAppFile = Constants.filePathWhatApp
        val saveAppFile = localDownloadPath

        if (whatAppFile.isDirectory) {

            listFile = whatAppFile.listFiles()

            if (listFile != null) {

                Arrays.sort(listFile!!, LastModifiedFileComparator.LASTMODIFIED_REVERSE)

                for (value in listFile!!) {

                    if (value.absoluteFile.toString().contains(".jpg")) {
                        imgPathWhatApp.add(ItemModel(value.absolutePath))
                    } else if (value.absoluteFile.toString().contains(".mp4")) {
                        videosPathWhatApp.add(ItemModel(value.absolutePath))
                    }

                }

            }

        }

        if (saveAppFile.isDirectory) {

            saveListFile = saveAppFile.listFiles()

            if (saveListFile != null) {

                Arrays.sort(saveListFile!!, LastModifiedFileComparator.LASTMODIFIED_REVERSE)

                for (value in saveListFile!!) {

                    savedPathWhatApp.add(ItemModel(value.absolutePath))

                }

            }

        }

        return true

    }

    fun readLocalEndData(boolean: Boolean) {

        lifecycleScope.launch {

            Constants.scopeIO.launch {

                val executionTimeOut = measureTimeMillis {

                    val job1: Deferred<Boolean> = async {
                        readWhatAppData()
                    }

                    if (job1.await()) {

                        if (boolean) {
                            updateHomeFragment(fileCopyTime)
                        } else {
                            updateHomeTagSaveWork(fileCopyTime)
                        }
                    }

                }

                fileCopyTime += executionTimeOut

                Log.e(TAG, "This is a time Job complete $fileCopyTime ms..")


            }
        }

    }

    suspend fun updateHomeTagSaveWork(timeStay: Long) {

        withContext(Dispatchers.Main) {

            dialog.show()

            if (dialog.isShowing) {

                object : CountDownTimer(timeStay, 1) {
                    override fun onTick(l: Long) {
                    }

                    override fun onFinish() {
                        Log.e(TAG, "dialog dismiss")
                        dialog.dismiss()

                        homeF.updateTabPosition(Constants.fragmentVisible)
                    }

                }.start()

            }

        }
    }

    private fun openHomeFragment() {

        Dexter.withContext(this@MainActivity)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {

                    readLocalEndData(true)

                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(
                        this@MainActivity,
                        "Uses-permission is Denied",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    homeRoot.visibility = View.GONE
                    permissionRoot.visibility = View.VISIBLE
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    fun navDrawMethod() {
        // new sliding navigation view
        //System.gc()
        /*slidingRootNav = SlidingRootNavBuilder(this@MainActivity).withMenuLayout(R.layout.newdrawer)
            .withDragDistance(120)
            .inject()*/

        slidingRootNav = SlidingRootNavBuilder(this)
            .withMenuLayout(R.layout.newdrawer)
            .withDragDistance(140) //Horizontal translation of a view. Default == 180dp
            .withRootViewScale(0.7f) //Content view's scale will be interpolated between 1f and 0.7f. Default == 0.65f;
            .withRootViewElevation(10) //Content view's elevation will be interpolated between 0 and 10dp. Default == 8.
            .withRootViewYTranslation(4) //Content view's translationY will be interpolated between 0 and 4. Default == 0
            .inject()

        navProBtn = findViewById(R.id.nav_proBtn)
        navRateUs = findViewById(R.id.rateus)
        navContactUs = findViewById(R.id.contact_us)
        navShareApp = findViewById(R.id.shareapp)
        navPrivacyPolicy = findViewById(R.id.privacy)
        navGuideApp = findViewById(R.id.nav_guide)

        navProBtn.setOnClickListener {
            openCloseNavigationView()

            object : CountDownTimer(300, 300) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    ProDialog(this@MainActivity, this@MainActivity)
                }

            }.start()
        }

        navContactUs.setOnClickListener {
            openCloseNavigationView()

            object : CountDownTimer(300, 300) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    FeedbackUtils.startFeedbackEmail(this@MainActivity)
                }

            }.start()

        }

        navShareApp.setOnClickListener {
            openCloseNavigationView()
            Utils.showToast(this, "under_dev")
            /*  val i = Intent(Intent.ACTION_SEND)
              i.type = "text/plain"
              i.putExtra(Intent.EXTRA_SUBJECT, "Label Maker")
              var sAux = "\nLet me recommend you this application\n\n"
              sAux = """
                  ${sAux}https://play.google.com/store/apps/details?id=com.labelcreator.label.maker
                  """.trimIndent()
              i.putExtra(Intent.EXTRA_TEXT, sAux)
              startActivity(Intent.createChooser(i, "choose one"))*/
        }

        navGuideApp.setOnClickListener {
            openCloseNavigationView()
            GuideAppDiaLog(this@MainActivity)
            /*  val i = Intent(Intent.ACTION_SEND)
              i.type = "text/plain"
              i.putExtra(Intent.EXTRA_SUBJECT, "Label Maker")
              var sAux = "\nLet me recommend you this application\n\n"
              sAux = """
                  ${sAux}https://play.google.com/store/apps/details?id=com.labelcreator.label.maker
                  """.trimIndent()
              i.putExtra(Intent.EXTRA_TEXT, sAux)
              startActivity(Intent.createChooser(i, "choose one"))*/
        }



        navPrivacyPolicy.setOnClickListener {
            openCloseNavigationView()
            object : CountDownTimer(300, 300) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    AboutAppDiaLog(this@MainActivity)
                }

            }.start()
            /* try {
                 startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://contentarcade.net/privacy-policy.php")))
             } catch (e: Exception) {
                 e.printStackTrace()
             }*/
        }

        navRateUs.setOnClickListener {
            openCloseNavigationView()

            object : CountDownTimer(300, 300) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    CustomRatingDialog(this@MainActivity)
                }

            }.start()
            //Utils.showToast(this, "under_dev")

            //rateUS()
        }

    }

    fun openCloseNavigationView() {

        if (slidingRootNav.isMenuClosed) {
            slidingRootNav.openMenu()
        } else {
            slidingRootNav.closeMenu()
        }

    }

    override fun onBackPressed() {
        if (slidingRootNav.isMenuOpened) {
            slidingRootNav.closeMenu()
        } else {
            //backPressDialog()
            finish()
        }
    }

    @SuppressLint("InflateParams")
    private fun backPressDialog() {

        val yes: TextView
        val no: TextView

        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.back_dialog, null)
        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) //before
        Objects.requireNonNull(dialog.window)
            ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        yes = view.findViewById(R.id.textView3)
        no = view.findViewById(R.id.textView4)
        dialog.setCancelable(false)
        dialog.show()
        no.setOnClickListener { dialog.dismiss() }
        yes.setOnClickListener {
            dialog.dismiss()
            finish()
        }

    }

    override fun onClickBuy() {
        Utils.showToast(this, "Click buy Button")
    }

    override fun itemSelectLong(filePath: ItemModel) {

        if (pathListSelect.isNotEmpty()) {

            if (filePath.isSelected) {
                Log.e("myTag", "${filePath.text}")
                pathListSelect.add(filePath)
            } else {
                Log.e("myTag", "${filePath.text}")
                pathListSelect.remove(filePath)
            }

        } else {
            pathListSelect.add(filePath)
            Log.e("myTag", "${filePath.text}")
        }

        if (pathListSelect.isNotEmpty()) {
            downloadBtn.visibility = View.VISIBLE
            reFreshData.visibility = View.GONE
        } else {
            downloadBtn.visibility = View.GONE
            reFreshData.visibility = View.VISIBLE
        }

        selectionCount = pathListSelect.size
        Log.e("myTag", "${pathListSelect.size}")
    }

    override fun itemClick(filePath: ItemModel) {

        WorkPreView(this@MainActivity, File(filePath.text), this)

    }

    override fun itemSelectSave(filePath: ItemModel) {}

    override fun downloadPreFile(uri: File) {

        var copyTime: Long? = null

        lifecycleScope.launch {

            Constants.scopeIO.launch {

                val executionTimeOut = measureTimeMillis {

                    val job1: Deferred<Boolean> = async {
                        downloadFile(uri)
                    }

                    if (job1.await()) {
                        //updateUiProgress(fileCopyTime)
                        updateFragmentUI()
                    }

                }

                Log.e(TAG, "This is a time Job complete $copyTime ms..")


            }
        }

    }

    override fun reFreshList() {

        lifecycleScope.launch {

            Constants.scopeIO.launch {
                updateFragmentUI()
            }
        }

    }

}