package com.risetech.whatsstatus.fragments

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.risetech.whatsstatus.R
import java.io.File


@Suppress("DEPRECATION")
class PreViewFragmentItem : Fragment() {

    var itemName: String? = ""
    lateinit var imageView: ImageView
    lateinit var videoRoot: FrameLayout
    var videoView: VideoView? = null
    var fileUri: Uri? = null
    lateinit var mContext: Context
    var viewLayout: View? = null

    lateinit var constraintLayout: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val extras = arguments
        itemName = extras!!.getString("itemPath")

        this.mContext = container!!.context

        val f = File(itemName!!)

        fileUri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(mContext, mContext.packageName + ".fileprovider", f)
        } else {
            Uri.fromFile(f)
        }

        if (itemName!!.contains(".mp4")) {

            viewLayout = inflater.inflate(R.layout.fragment_pre_view_item, container, false)
            videoRoot = viewLayout!!.findViewById(R.id.video_root)
            videoView = viewLayout!!.findViewById(R.id.video_view)

            constraintLayout = viewLayout!!.findViewById(R.id.imageRoot)
            imageView = viewLayout!!.findViewById(R.id.imageView7)

            Glide.with(mContext)
                .load(fileUri)
                .into(imageView)

            videoView!!.setVideoURI(fileUri)
            val mediaController = MediaController(mContext)
            videoView!!.setMediaController(mediaController)
            mediaController.setAnchorView(videoView)

            constraintLayout.setOnClickListener {

                videoRoot.visibility = View.VISIBLE
                videoView!!.visibility = View.VISIBLE
                videoView!!.start()
                constraintLayout.visibility = View.GONE
            }

            videoView!!.setOnCompletionListener {
                // Video Playing is completed

                videoRoot.visibility = View.GONE
                constraintLayout.visibility = View.VISIBLE
            }

        } else {

            viewLayout = inflater.inflate(R.layout.item_preview, container, false)
            imageView = viewLayout!!.findViewById(R.id.imagePreView)
            imageView.setImageURI(fileUri)

        }

        return viewLayout
    }

    companion object {
        @JvmStatic
        fun newInstance(categoryName: String?): PreViewFragmentItem {
            val args = Bundle()
            args.putString("itemPath", categoryName)
            val fragment = PreViewFragmentItem()
            fragment.arguments = args
            return fragment
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            // load data here      resumePlayer();
            Log.e("myTag", "true")

        } else {
            // fragment is no longer visible    pausePlayer();
            Log.e("myTag", "false")

            if (videoView != null) {
                videoView!!.stopPlayback()
                videoRoot.visibility = View.GONE
                videoView!!.visibility = View.GONE
                constraintLayout.visibility = View.VISIBLE

            }
        }
    }

}