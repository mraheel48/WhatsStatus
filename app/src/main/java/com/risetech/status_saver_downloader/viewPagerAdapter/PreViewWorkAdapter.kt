package com.risetech.status_saver_downloader.viewPagerAdapter

import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.risetech.status_saver_downloader.R
import com.risetech.status_saver_downloader.dataModel.ItemModel
import java.io.File
import java.util.*

class PreViewWorkAdapter(var path: ArrayList<ItemModel>) :
    RecyclerView.Adapter<PreViewWorkAdapter.ViewHolder>() {

    var mContext: Context? = null
    var fileUri: Uri? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_preview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


      /*  if (path[position].text.contains(".jpg")) {
            holder.vidThumbNail.visibility = View.GONE
        }*/

        val f = File(path[position].text)
        fileUri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(mContext!!, mContext!!.packageName + ".fileprovider", f)
        } else {
            Uri.fromFile(f)
        }

        Glide.with(mContext!!)
            .load(fileUri)
            .into(holder.thumbNail)


    }

    override fun getItemCount(): Int {
        return path.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var thumbNail: ImageView = itemView.findViewById(R.id.imagePreView)
        /*var vidThumbNail: ImageView = itemView.findViewById(R.id.VidplaceHolder)
        var selectImage: ImageView = itemView.findViewById(R.id.imageView4)*/

        init {

            /*thumbNail.setOnClickListener {
                Toast.makeText(mContext, "Click", Toast.LENGTH_SHORT).show()
            }*/

        }
    }

}