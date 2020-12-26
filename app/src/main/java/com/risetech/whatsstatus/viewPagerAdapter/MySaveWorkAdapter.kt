package com.risetech.whatsstatus.viewPagerAdapter

import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.risetech.whatsstatus.R
import com.risetech.whatsstatus.activities.MainActivity
import com.risetech.whatsstatus.dataModel.ItemModel
import java.io.File
import java.util.*

class MySaveWorkAdapter(var path: ArrayList<ItemModel>) :
    RecyclerView.Adapter<MySaveWorkAdapter.ViewHolder>() {

    var mContext: Context? = null
    var fileUri: Uri? = null
    lateinit var onClickItem: ItemClick

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(parent.context).inflate(R.layout.re_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /*if (!path[position].isSelected) {
            holder.selectImage.visibility = View.GONE
        } else {
            path[position].isSelected = true
            holder.selectImage.visibility = View.VISIBLE
        }*/

        if (path[position].text.contains(".jpg")) {
            holder.vidThumbNail.visibility = View.GONE
        }

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

        var thumbNail: ImageView = itemView.findViewById(R.id.placeHolder)
        var vidThumbNail: ImageView = itemView.findViewById(R.id.VidplaceHolder)
        var selectImage: ImageView = itemView.findViewById(R.id.imageView4)

        init {

            onClickItem = mContext as MainActivity

            thumbNail.setOnClickListener {
                /* path[adapterPosition].isSelected = !path[adapterPosition].isSelected
                 onClickItem.itemSelectSave(path[adapterPosition])
                 notifyDataSetChanged()*/

                Toast.makeText(mContext, "Click", Toast.LENGTH_SHORT).show()
            }

        }
    }

    interface ItemClick {
        fun itemSelectSave(filePath: ItemModel)
    }
}