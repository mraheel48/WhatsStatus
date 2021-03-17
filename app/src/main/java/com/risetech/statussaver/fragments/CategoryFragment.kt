package com.risetech.statussaver.fragments

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.risetech.statussaver.R
import com.risetech.statussaver.dataModel.ItemModel
import com.risetech.statussaver.utils.Constants
import com.risetech.statussaver.utils.Utils
import com.risetech.statussaver.viewPagerAdapter.MyWorkAdapter
import org.apache.commons.io.comparator.LastModifiedFileComparator
import java.io.File
import java.util.*

class CategoryFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var mContext: Context
    var categoryName: String? = ""

    var imgPath: ArrayList<ItemModel> = ArrayList()
    var videoPath: ArrayList<ItemModel> = ArrayList()

    var savePathFile: ArrayList<ItemModel> = ArrayList()

    var listFile: Array<File>? = null

    var myWorkAdapter: MyWorkAdapter? = null

    lateinit var localDownloadPath: File
    lateinit var permissionRoot: ConstraintLayout
    lateinit var btnRefreshPer: Button


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_category, container, false)

        mContext = container!!.context

        recyclerView = view.findViewById(R.id.recyclerView)
        permissionRoot = view.findViewById(R.id.permissionRoot)
        btnRefreshPer = view.findViewById(R.id.btnRefresh)

        recyclerView.setHasFixedSize(true)
        localDownloadPath = Constants.fileDownloadPath

        val extras = arguments
        categoryName = extras!!.getString("cate_name")

        if (categoryName == "images") {
            Log.e("myTab", "${categoryName}")
        } else if (categoryName == "videos") {

        } else {

        }


        /*readLocalData()

        btnRefreshPer.setOnClickListener {
            //readLocalData()
            Utils.showToast(mContext,"calling Prmissoin")
        }*/

        /*if (categoryName == "images") {
            myWorkAdapter = MyWorkAdapter((mContext as MainActivity).imgPathWhatApp)
            recyclerView.adapter = myWorkAdapter
        }

        if (categoryName == "videos") {
            myWorkAdapter = MyWorkAdapter((mContext as MainActivity).videosPathWhatApp)
            recyclerView.adapter = myWorkAdapter
        }

        if (categoryName == "saved") {
            myWorkAdapter = MyWorkAdapter((mContext as MainActivity).savedPathWhatApp)
            recyclerView.adapter = myWorkAdapter
        }*/

        return view
    }


    fun readLocalData() {

        Dexter.withContext(mContext)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    permissionRoot.visibility = View.GONE
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
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

    companion object {
        @JvmStatic
        fun newInstance(categoryName: String?): CategoryFragment {
            val args = Bundle()
            args.putString("cate_name", categoryName)
            val fragment = CategoryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    val imgFromSdcard: Unit
        get() {
            imgPath.clear()

            val file = Constants.filePathWhatApp

            if (file.isDirectory) {

                listFile = file.listFiles()

                if (listFile != null) {

                    Arrays.sort(listFile!!, LastModifiedFileComparator.LASTMODIFIED_REVERSE)

                    for (value in listFile!!) {

                        if (value.absoluteFile.toString().contains(".jpg")) {

                            imgPath.add(ItemModel(value.absolutePath))

                            if (imgPath.isNotEmpty()) {

                                myWorkAdapter = MyWorkAdapter(imgPath)

                                recyclerView.adapter = myWorkAdapter

                            }
                        }

                    }

                }

            } else {
                Utils.showToast(mContext, "Path not found")
            }

        }

    val videoFromSdcard: Unit
        get() {
            imgPath.clear()

            val file = Constants.filePathWhatApp

            if (file.isDirectory) {

                listFile = file.listFiles()

                if (listFile != null) {

                    Arrays.sort(listFile!!, LastModifiedFileComparator.LASTMODIFIED_REVERSE)

                    for (value in listFile!!) {

                        if (value.absoluteFile.toString().contains(".mp4")) {

                            imgPath.add(ItemModel(value.absolutePath))

                            if (imgPath.isNotEmpty()) {

                                myWorkAdapter = MyWorkAdapter(imgPath)
                                recyclerView.adapter = myWorkAdapter

                            }
                        }

                    }

                }

            } else {
                Utils.showToast(mContext, "Path not found")

            }

        }


}