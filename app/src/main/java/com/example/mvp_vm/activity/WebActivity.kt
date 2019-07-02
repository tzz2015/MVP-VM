package com.example.mvp_vm.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.mvp_vm.R
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        initView()
        vb.loadUrl("file:///android_asset/SchemaLaunch.html")
//        vb.loadUrl("https://www.baidu.com")

    }

    private fun initView() {
        initWebClient()
        bt_jump.setOnClickListener {
            try {
                var url = "fubei://platformapi/startapp?appId=%s&action=%s"
                val jumpUrl = String.format(url, input_app_id.text.toString(), input_action.text.toString())
                Log.e("", jumpUrl)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(jumpUrl))
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(javaClass.simpleName, e.message)
            }

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebClient() {
        val webSetting = vb.settings
        webSetting.javaScriptEnabled = true
        vb.webViewClient = object : WebViewClient() {


            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                handler.proceed()
                super.onReceivedSslError(view, handler, error)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Log.e("url", url)
                if (url.startsWith("http") || url.startsWith("https")) {
                    // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                    view.loadUrl(url)
                    return true
                }
                if (url.startsWith("taobao://")) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        return true
                    } catch (e: Exception) {
                        Toast.makeText(this@WebActivity, "请安装淘宝", Toast.LENGTH_SHORT).show()
                    }
                }


                return super.shouldOverrideUrlLoading(view, url)
            }

        }
    }
}


