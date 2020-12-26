package com.risetech.whatsstatus.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.risetech.whatsstatus.R
import com.risetech.whatsstatus.activities.MainActivity
import com.risetech.whatsstatus.dataModel.ItemModel
import com.risetech.whatsstatus.utils.Constants
import com.risetech.whatsstatus.utils.Utils
import com.risetech.whatsstatus.viewPagerAdapter.MySaveWorkAdapter
import com.risetech.whatsstatus.viewPagerAdapter.MyWorkAdapter
import org.apache.commons.io.comparator.LastModifiedFileComparator
import java.io.File
import java.util.*

class CategoryFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var mContext: Context
    var categoryName: String? = ""

    //var imgPath: Array<ItemModel> = ArrayList()

    var imgPath: ArrayList<ItemModel> = ArrayList()

    var savePathFile: ArrayList<ItemModel> = ArrayList()

    var listFile: Array<File>? = null

    var myWorkAdapter: MyWorkAdapter? = null
    var mySaveWorkAdapter: MySaveWorkAdapter? = null

    lateinit var localDownloadPath: File


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_category, container, false)

        val view = inflater.inflate(R.layout.fragment_category, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.setHasFixedSize(true)

        mContext = container!!.context

        localDownloadPath = Utils.fileDownloadPath(mContext)

        val extras = arguments
        categoryName = extras!!.getString("cate_name")
        //Log.e("myTag","${categoryName}")

        if (categoryName == "images") {
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
        }


        return view
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

    val saveSDcard: Unit
        get() {
            savePathFile.clear()

            val file = localDownloadPath

            if (file.isDirectory) {

                listFile = file.listFiles()

                if (listFile != null) {

                    Arrays.sort(listFile!!, LastModifiedFileComparator.LASTMODIFIED_REVERSE)

                    for (value in listFile!!) {

                        if (value.absoluteFile.toString()
                                .contains(".mp4") || value.absoluteFile.toString().contains(".jpg")
                        ) {

                            savePathFile.add(ItemModel(value.absolutePath))

                            if (savePathFile.isNotEmpty()) {

                                mySaveWorkAdapter = MySaveWorkAdapter(savePathFile)
                                recyclerView.adapter = mySaveWorkAdapter

                            }
                        }


                    }

                }

            } else {
                Utils.showToast(mContext, "Path not found")
            }

        }


}