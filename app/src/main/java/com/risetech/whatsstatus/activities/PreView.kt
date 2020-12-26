package com.risetech.whatsstatus.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.risetech.whatsstatus.R
import com.risetech.whatsstatus.dataModel.ItemModel
import com.risetech.whatsstatus.fragments.PreViewFragment


class PreView : AppCompatActivity() {

    var passList: ArrayList<ItemModel> = ArrayList()

    lateinit var preView: PreViewFragment
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction

    lateinit var pagerViewRoot: ConstraintLayout

    var position: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_view)

        if (intent.getStringExtra("itemPosition") != null) {
            position = intent.getStringExtra("itemPosition")
        }

        pagerViewRoot = findViewById(R.id.pagerViewRoot)

        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()

        preView = PreViewFragment()

        val bundle = Bundle()
        bundle.putString("itemPosition", position)
        preView.arguments = bundle

        fragmentTransaction
            .replace(R.id.pagerViewRoot, preView)
            .commit()


        //passList.addAll(Constants.passList)

        /*recyclerView = findViewById(R.id.workPreView)
        recyclerView.setHasFixedSize(true)

        myPreViewWork = PreViewWorkAdapter(passList)

        recyclerView.adapter = myPreViewWork*/

        //Log.e("myTag", "${passList.size}")


    }

}