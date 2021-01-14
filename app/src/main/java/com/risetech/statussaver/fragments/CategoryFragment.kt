package com.risetech.statussaver.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.risetech.statussaver.R
import com.risetech.statussaver.activities.MainActivity
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

    var savePathFile: ArrayList<ItemModel> = ArrayList()

    var listFile: Array<File>? = null

    var myWorkAdapter: MyWorkAdapter? = null

    lateinit var localDownloadPath: File


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_category, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.setHasFixedSize(true)

        mContext = container!!.context

        localDownloadPath = Constants.fileDownloadPath
        //localDownloadPath = Utils.fileDownloadPath(mContext)

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


}