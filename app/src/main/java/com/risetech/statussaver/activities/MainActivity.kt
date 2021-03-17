package com.risetech.statussaver.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.Purchase
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.tabs.TabLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.risetech.statussaver.BuildConfig
import com.risetech.statussaver.R
import com.risetech.statussaver.ads.AdManager
import com.risetech.statussaver.billing.GoogleBilling
import com.risetech.statussaver.dataModel.ItemModel
import com.risetech.statussaver.dialogs.*
import com.risetech.statussaver.utils.Constants
import com.risetech.statussaver.utils.FeedbackUtils
import com.risetech.statussaver.utils.Utils
import com.risetech.statussaver.viewPagerAdapter.CustomViewPagerAdapter
import com.risetech.statussaver.viewPagerAdapter.MyWorkAdapter
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.coroutines.*
import org.apache.commons.io.comparator.LastModifiedFileComparator
import java.io.*
import java.util.*
import kotlin.system.measureTimeMillis

@Suppress("UNNECESSARY_SAFE_CALL")
class MainActivity : AppCompatActivity(), ProDialog.BuyClick, MyWorkAdapter.ItemClick,
    WorkPreView.DownloadFile, AdManager.CallbackInterstial, GoogleBilling.GoogleBillingHandler {

    lateinit var navBtn: ImageView
    lateinit var drawer: DrawerLayout
    lateinit var homeRoot: ConstraintLayout

    //nav_Root
    lateinit var slidingRootNav: SlidingRootNav
    lateinit var navProBtn: ConstraintLayout
    lateinit var navRateUs: ConstraintLayout
    lateinit var navContactUs: ConstraintLayout
    lateinit var navShareApp: ConstraintLayout
    lateinit var navGuideApp: ConstraintLayout
    lateinit var navPrivacyPolicy: ConstraintLayout
    lateinit var navAbout: ConstraintLayout

   // var homeF = HomeFragment()
    /*private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction*/

    var pathListSelect: ArrayList<ItemModel> = ArrayList()

    lateinit var downloadBtn: ImageView
    lateinit var reFreshData: ImageView

    private lateinit var customDialogBuilder: AlertDialog.Builder
    private lateinit var customDialog: AlertDialog

    val TAG = "MainActivity"

    var imgPathWhatApp: ArrayList<ItemModel> = ArrayList()
    var videosPathWhatApp: ArrayList<ItemModel> = ArrayList()
    var savedPathWhatApp: ArrayList<ItemModel> = ArrayList()
    var passList: ArrayList<ItemModel> = ArrayList()

    private lateinit var dialog: Dialog
    var fileCount = 0
    var selectionCount = 0
    var fileCopyTime: Long = 1000

    var listFile: Array<File>? = null
    var saveListFile: Array<File>? = null
    var myWorkAdapter: MyWorkAdapter? = null

    lateinit var localDownloadPath: File

    //google Ads
    var adView: AdView? = null
    var adLayout: FrameLayout? = null

    lateinit var bp: GoogleBilling
    lateinit var tvTitle: TextView

    lateinit var viewPager: ViewPager
    lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init Id Layout
        navBtn = findViewById(R.id.nav_btn)
        drawer = findViewById(R.id.drawer_layout)
        homeRoot = findViewById(R.id.fragmentRoot)
        downloadBtn = findViewById(R.id.nav_btn2)
        reFreshData = findViewById(R.id.refresh_data)
        adLayout = findViewById(R.id.adLayout)
        tvTitle = findViewById(R.id.title)

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tabs)

        viewPager.offscreenPageLimit = 0
        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)

        //Billing init
        bp = GoogleBilling(this@MainActivity, this@MainActivity, this)
        bpInit()

        //windows dialog code
        dialog = Dialog(this@MainActivity)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dilog_svg_loader)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)

        localDownloadPath = Constants.fileDownloadPath
        Log.e(TAG, "${localDownloadPath}")

        //set nav_Draw
        navDrawMethod()

       /* fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()*/

        navBtn.setOnClickListener {
            openCloseNavigationView()
        }

        downloadBtn.setOnClickListener {

            /* downloadBtn.isClickable = false
             Constants.showAds = true

            // Utils.showToast(this, "download Click")
             copyFileBG()*/
        }

        reFreshData.setOnClickListener {
            /* reFreshData.isClickable = false
             updateFragmentUI()*/
        }

       /* fragmentTransaction
            .replace(R.id.fragmentRoot, homeF)
            .commit()*/

        /* if (BuildConfig.DEBUG) {

             tvTitle?.let { it ->

                 it.setOnClickListener {

                     it.isClickable = false

                     if (bp.isConnected) {

                         bp.consumePurchase(Constants.inAppKey) { error: Int?, _: String? ->
                             if (error == null) {
                                 it.isClickable = true
                                 Utils.showToast(this@MainActivity, "Billing Prossor is consume")

                             } else {
                                 Log.e("myTag", "Error not billing Consume")
                             }
                         }

                     } else {
                         Utils.showToast(this@MainActivity, "Billing Prossor is not conntect")
                         it.isClickable = true

                     }

                 }
             }
         }*/

        //openHomeFragment()

    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = CustomViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.currentItem = 0
    }


    private fun bpInit() {
        if (!bp.isConnected) {
            bp.startConnection()
        }
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
                        updateUiProgress()
                        //Log.e(TAG, "else ${fileCopyTime
                    }

                }

                fileCopyTime += executionTimeOut

                Log.e(TAG, "This is a time Job complete $fileCopyTime ms..")

            }
        }
    }

    suspend fun updateUiProgress() {
        withContext(Dispatchers.Main) {
            updateFragmentUI()
        }
    }

    //This method uner the dev...
    suspend fun updateHomeFragment(timeStay: Long) {

        withContext(Constants.mainDispatcher) {

            // Log.e("myTag", "${Thread.currentThread()}")

            dialog.show()

            if (dialog.isShowing) {

                object : CountDownTimer(timeStay, 1) {

                    override fun onTick(l: Long) {}

                    override fun onFinish() {

                        Log.e(TAG, "dialog dismiss")

                        dialog.dismiss()

                        homeRoot.visibility = View.VISIBLE


                    }

                }.start()

            }
        }


    }

    private fun showAds() {
        if (bp.isConnected && bp.isPurchased(Constants.inAppKey)) {
            Log.e("myTag", "User Pro")
        } else {
            AdManager.showInterstial(this, this)
        }
    }

    fun updateFragmentUI() {

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

        if (Constants.showAds) {
            Constants.showAds = !Constants.showAds
            showAds()
        }
    }

    suspend fun copyFileResult(fileNumber: Int): Int {

        File(pathListSelect[fileNumber].text).copyTo(
            File(
                localDownloadPath.toString() + pathListSelect[fileNumber].text
                    .replace(Constants.filePathWhatApp.toString(), "")
            ), true
        )


        val filePath = localDownloadPath.toString() + pathListSelect[fileNumber].text.replace(
            Constants.filePathWhatApp.toString(),
            ""
        )

        MediaScannerConnection.scanFile(
            this@MainActivity,
            arrayOf(filePath),
            arrayOf("image/jpeg/video/mp4"),
            null
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
                        //homeF.updateTabPosition(Constants.fragmentVisible)
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
                    //  homeF.updateData()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {

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
        navPrivacyPolicy = findViewById(R.id.nav_policy)
        navAbout = findViewById(R.id.privacy)
        navGuideApp = findViewById(R.id.nav_guide)

        navProBtn.setOnClickListener {
            openCloseNavigationView()
            object : CountDownTimer(300, 300) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    // ProDialog(this@MainActivity, this@MainActivity)
                    val i = Intent(applicationContext, ProScreen::class.java)
                    startActivity(i)
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
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, "Status Saver")
            var sAux = "\nLet me recommend you this application\n\n"
            sAux = """
                  ${sAux}https://play.google.com/store/apps/details?id=com.risetech.status.downloader.saver.story
                  """.trimIndent()
            i.putExtra(Intent.EXTRA_TEXT, sAux)
            startActivity(Intent.createChooser(i, "choose one"))
        }

        navGuideApp.setOnClickListener {

            openCloseNavigationView()
            object : CountDownTimer(300, 300) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    GuideAppDiaLog(this@MainActivity)
                }
            }.start()


        }

        navAbout.setOnClickListener {
            openCloseNavigationView()
            object : CountDownTimer(300, 300) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    AboutAppDiaLog(this@MainActivity)
                }

            }.start()

        }

        navPrivacyPolicy.setOnClickListener {

            openCloseNavigationView()
            object : CountDownTimer(300, 300) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    try {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://weberrorfinder.com/risetech-privacy-policy.html")
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()


        }

        navRateUs.setOnClickListener {
            openCloseNavigationView()
            object : CountDownTimer(300, 300) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    CustomRatingDialog(this@MainActivity)
                }
            }.start()
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

    override fun itemClick(filePath: ItemModel, position: Int) {

        //passList.clear()
        Constants.passList.clear()

        if (Constants.fragmentVisible == 0) {
            Constants.passList.addAll(imgPathWhatApp)
        } else if (Constants.fragmentVisible == 1) {
            Constants.passList.addAll(videosPathWhatApp)
        } else {
            Constants.passList.addAll(savedPathWhatApp)
        }

        if (Constants.passList.isNotEmpty()) {
            nextActivity(position)
        }

    }

    private fun nextActivity(position: Int) {
        val intent = Intent(this@MainActivity, PreView::class.java)
        intent.putExtra("itemPosition", position.toString())
        startActivity(intent)
    }

    override fun downloadPreFile(uri: File) {

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

                Log.e(TAG, "This is a time Job complete $executionTimeOut ms..")

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

    override fun onResume() {
        super.onResume()

        /*if (bp.isConnected && bp.isPurchased(Constants.inAppKey)) {
            adLayout?.visibility = View.GONE
        } else {
            adLayout?.visibility = View.VISIBLE

            if (Utils.isNetworkAvailable(this)){
                adLayout?.visibility = View.VISIBLE
                adLayout?.post { loadBanner() }
                AdManager.loadInterstial(this@MainActivity, this)
            }
        }

        if (Constants.fileStatus) {
            Constants.fileStatus = false
            Constants.scopeIO.launch {
                updateFragmentUI()
            }
        }*/

    }

    override fun onBillingInitialized() {

        /*if (bp.isPurchased(Constants.inAppKey)) {
            adLayout?.visibility = View.GONE
            Constants.inAppPrices = "Already Purchased"
        } else {

            if (Utils.isNetworkAvailable(this)){
                adLayout?.visibility = View.VISIBLE
                adLayout?.post { loadBanner() }
                AdManager.loadInterstial(this@MainActivity, this)
            }

        }*/

    }

    override fun onPurchased(purchase: Purchase) {

    }

    override fun onBillingServiceDisconnected() {
    }

    override fun onBillingError(errorCode: Int) {

        /* if (GoogleBilling.ResponseCodes.BILLING_UNAVAILABLE == errorCode) {
             Log.e("myTag", "${errorCode}-- calling Banner")
             if (Utils.isNetworkAvailable(this)){
                 adLayout?.visibility = View.VISIBLE
                 adLayout?.post { loadBanner() }
                 AdManager.loadInterstial(this@MainActivity, this)
             }
         }*/

    }

    private fun loadBanner() {

        adView = AdView(this)

        if (BuildConfig.DEBUG) {
            adView?.adUnitId = Constants.bannerTestId
        } else {
            adView?.adUnitId = Constants.bannerId
        }

        val adSize = adSize
        adView!!.adSize = adSize
        adLayout!!.removeAllViews()
        adLayout!!.addView(adView)
        val adRequest = AdRequest.Builder().build()
        // Start loading the ad in the background.
        try {
            adView!!.loadAd(adRequest)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    // Determine the screen width (less decorations) to use for the ad width.
    @Suppress("DEPRECATION")
    private val adSize: AdSize
        get() {
            // Determine the screen width (less decorations) to use for the ad width.
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val density = outMetrics.density
            var adWidthPixels = adLayout!!.width.toFloat()
            // If the ad hasn't been laid out, default to the full screen width.
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationBannerAdSizeWithWidth(this, adWidth)
        }


    override fun onAdLoaded() {

    }

    override fun onAdFailedToLoad(errorCode: Int) {

    }

    override fun onAdOpened() {

    }

    override fun onAdClicked() {

    }

    override fun onAdLeftApplication() {

    }

    override fun onAdClosed() {

    }

}