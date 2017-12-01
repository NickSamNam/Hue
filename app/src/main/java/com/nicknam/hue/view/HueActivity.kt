package com.nicknam.hue.view

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.nicknam.hue.R
import com.nicknam.hue.model.Hub
import kotlinx.android.synthetic.main.activity_hue.*

class HueActivity : AppCompatActivity(), ControllableAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, Hub.RequestListener {
    override fun onSuccessful() {
        activity_hue_swipeContainer.isRefreshing = false
        activity_hue_rv_controllable.adapter.notifyDataSetChanged()
    }

    override fun onFailed() {
        activity_hue_swipeContainer.isRefreshing = false
        Toast.makeText(this@HueActivity, R.string.error_update_failure, Toast.LENGTH_SHORT).show()
    }

    override fun onRefresh() {
        _hub.updateAllLights(this)
        _hub.updateAllGroups(this)
    }

    private lateinit var _hub: Hub
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var contrAdapter: ControllableAdapter? = null
        var result = false
        when (item.itemId) {
            R.id.navigation_lamps -> {
                _hub.updateAllLights(this)
                contrAdapter = ControllableAdapter(_hub.Lights)
                result = true
            }
            R.id.navigation_groups -> {
                _hub.updateAllGroups(this)
                contrAdapter = ControllableAdapter(_hub.Groups)
                result = true
            }
        }
        contrAdapter?.setOnItemClickListener(this)
        activity_hue_rv_controllable.swapAdapter(contrAdapter, true)
        return@OnNavigationItemSelectedListener result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hue)
        activity_hue_rv_controllable.layoutManager = LinearLayoutManager(this)

        _hub = Hub.getInstance()

        activity_hue_swipeContainer.setOnRefreshListener(this)

        var contrAdapter: ControllableAdapter? = null

        if (activity_hue_nav_navigation.selectedItemId == R.id.navigation_lamps) {
            contrAdapter = ControllableAdapter(_hub.Lights)
            if (savedInstanceState == null)
                _hub.updateAllLights(this)
        }
        else if (activity_hue_nav_navigation.selectedItemId == R.id.navigation_groups) {
            contrAdapter = ControllableAdapter(_hub.Groups)
            if (savedInstanceState == null)
                _hub.updateAllGroups(this)
        }

        activity_hue_nav_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        contrAdapter?.setOnItemClickListener(this)
        activity_hue_rv_controllable.adapter = contrAdapter
    }

    override fun onItemClick(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
