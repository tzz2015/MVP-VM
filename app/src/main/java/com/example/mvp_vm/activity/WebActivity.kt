package com.example.mvp_vm.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.mvp_vm.R
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        vb.loadUrl("file:///android_asset/SchemaLaunch.html")
        initView()
    }

    private fun initView() {
        bt_jump.setOnClickListener {
            try {
                var url = "fubei://platformapi/startapp?appId=%s&action=%s"
                val jumpUrl = String.format(url, input_app_id.text.toString(), input_action.toString())
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(jumpUrl))
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(javaClass.simpleName, e.message)
            }

        }


    }
}
