package com.risetech.statussaver.viewPagerAdapter

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
import com.risetech.statussaver.R
import com.risetech.statussaver.activities.MainActivity
import com.risetech.statussaver.dataModel.ItemModel
import com.risetech.statussaver.utils.Constants
import java.io.File
import java.util.*

class MyWorkAdapter(val path: ArrayList<ItemModel>) : RecyclerView.Adapter<MyWorkAdapter.ViewHolder>() {

    var mContext: Context? = null
    var fileUri: Uri? = null
    lateinit var onClickItem: ItemClick
    var enableLongClick: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.re_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (path[position].isSelected) {
            holder.selectImage.visibility = View.VISIBLE
        } else {
            holder.selectImage.visibility = View.GONE
        }

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

            itemView.setOnClickListener {

                if (Constants.fragmentVisible == 2) {
                    //Toast.makeText(mContext, "calling save", Toast.LENGTH_SHORT).show()
                    onClickItem.itemClick(path[adapterPosition], adapterPosition)

                } else {

                    if (enableLongClick && (mContext as MainActivity).selectionCount > 0) {

                        if (path[adapterPosition].isSelected) {
                            path[adapterPosition].isSelected = false
                        } else {
                            path[adapterPosition].isSelected = true
                        }

                        onClickItem.itemSelectLong(path[adapterPosition])
                        notifyItemChanged(adapterPosition)

                    } else {

                        //Toast.makeText(mContext, "Simple Click", Toast.LENGTH_SHORT).show()
                        onClickItem.itemClick(path[adapterPosition], adapterPosition)
                    }

                }

            }

            itemView.setOnLongClickListener {
                /* val p = layoutPosition
                 println("LongClick: $p")*/

                if (Constants.fragmentVisible == 2) {

                    //Toast.makeText(mContext, "calling save", Toast.LENGTH_SHORT).show()
                    onClickItem.itemClick(path[adapterPosition], adapterPosition)

                } else {

                    if (path[adapterPosition].isSelected) {
                        path[adapterPosition].isSelected = false
                    } else {
                        path[adapterPosition].isSelected = true
                    }

                    enableLongClick = true

                    onClickItem.itemSelectLong(path[adapterPosition])
                    notifyItemChanged(adapterPosition)
                }

                true // returning true instead of false, works for me
            }


        }
    }

    interface ItemClick {
        fun itemSelectLong(filePath: ItemModel)
        fun itemClick(filePath: ItemModel, position: Int)
    }

}