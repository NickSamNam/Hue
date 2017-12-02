package com.nicknam.hue.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import android.widget.Toast
import com.nicknam.hue.R
import com.nicknam.hue.model.Hub
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Hub.CreateUserListener, Hub.RequestListener {
    override fun onFailed() {
        Toast.makeText(this, R.string.error_hub_not_found, Toast.LENGTH_SHORT).show()
    }

    override fun onSuccessful() {
        nextActivity()
    }

    override fun onSuccessful(username: String) {
        getSharedPreferences("hue", Context.MODE_PRIVATE).edit().putString("username", username).apply()
        nextActivity()
    }

    override fun onCreationFailed() {
        Toast.makeText(this, R.string.error_createUser, Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sp = getSharedPreferences("hue", Context.MODE_PRIVATE)

        activity_main_btn_confirm.setOnClickListener {
            val ipAddr = activity_main_et_ip.text.toString()
            if (Patterns.IP_ADDRESS.matcher(ipAddr).matches()) {
                val hub = Hub.getInstance()
                hub.init(ipAddr, applicationContext, this)

                if (ipAddr == "145.48.205.33")
                    hub.Username = "iYrmsQq1wu5FxF9CPqpJCnm1GpPVylKBWDUsNDhB"
                else {
                    if (sp.contains("username")) {
                        hub.Username = sp.getString("username", "")
                    } else
                        hub.createUser(application.packageName, this)
                }
            } else {
                Toast.makeText(this, R.string.error_ipaddr, Toast.LENGTH_SHORT).show()
            }
        }

        activity_main_btn_reset.setOnClickListener({ sp.edit().clear().apply() })
    }

    private fun nextActivity() {
        startActivity(Intent(this, HueActivity::class.java))
    }
}
