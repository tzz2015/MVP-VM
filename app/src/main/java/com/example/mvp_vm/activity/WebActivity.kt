package com.example.mvp_vm.activity

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.*
import android.widget.Toast
import com.example.mvp_vm.utils.PhotoUtils
import com.example.mvp_vm.utils.toast
import kotlinx.android.synthetic.main.activity_web.*
import java.io.File


class WebActivity : AppCompatActivity() {
    private val TAG: String = "WebActivity"
    private var mUploadMessage: ValueCallback<Uri>? = null
    private var mUploadCallbackAboveL: ValueCallback<Array<Uri>>? = null
    private val PHOTO_REQUEST = 100
    private val VIDEO_REQUEST = 120
    private val fileUri = File(getExternalStorageDirectory().getPath() + "/" + "temp.jpg")
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mvp_vm.R.layout.activity_web)
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
        webSetting.domStorageEnabled = true
        webSetting.allowFileAccess = true
        webSetting.allowContentAccess = true
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
        // 拦截视频 相册
        vb.webChromeClient = object : WebChromeClient() {

            // Android 3~4.1
            fun openFileChooser(valueCallback: ValueCallback<Uri>, acceptType: String) {
                toast(acceptType)
                mUploadMessage = valueCallback
                // 录制视频
                if (acceptType.startsWith("video")) {
                    recordVideo()
                } else {
                    takePhoto()
                }
            }

            // Android  4.1以上
            fun openFileChooser(valueCallback: ValueCallback<Uri>, acceptType: String, capture: String) {
                toast(acceptType)
                mUploadMessage = valueCallback
                // 录制视频
                if (acceptType.startsWith("video")) {
                    recordVideo()
                } else {
                    takePhoto()
                }
            }

            // Android 5.0以上
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                val acceptTypes = fileChooserParams.acceptTypes
                toast(acceptTypes[0])
                mUploadCallbackAboveL = filePathCallback
                // 录制视频
                if (acceptTypes[0].startsWith("video")) {
                    recordVideo()
                } else {
                    takePhoto()
                }
                return true
            }
        }
    }

    /**
     * 打开相册或者拍照
     */
    private fun takePhoto() {
        try {
            imageUri = Uri.fromFile(fileUri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //通过FileProvider创建一个content类型的Uri
                imageUri = FileProvider.getUriForFile(this, packageName + ".fileprovider", fileUri)
            }
            PhotoUtils.takePicture(this, imageUri, PHOTO_REQUEST)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }

    /**
     * 录制视频
     */
    private fun recordVideo() {
        try {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
            //限制时长
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10)
            //开启摄像机
            startActivityForResult(intent, VIDEO_REQUEST)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        vb.webChromeClient = null
        vb.webChromeClient = null
        vb.destroy()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == PHOTO_REQUEST) {
                if (null == mUploadMessage && null == mUploadCallbackAboveL) return
                val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
                if (mUploadCallbackAboveL != null) {
                    onActivityResultAboveL(requestCode, resultCode, data)
                } else if (mUploadMessage != null) {
                    mUploadMessage!!.onReceiveValue(result)
                    mUploadMessage = null
                }
            } else if (requestCode == VIDEO_REQUEST) {
                if (null == mUploadMessage && null == mUploadCallbackAboveL) return

                val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
                if (mUploadCallbackAboveL != null) {
                    if (resultCode == Activity.RESULT_OK) {
                        mUploadCallbackAboveL!!.onReceiveValue(result?.let { arrayOf<Uri>(it) })
                        mUploadCallbackAboveL = null
                    } else {
                        mUploadCallbackAboveL!!.onReceiveValue(arrayOf())
                        mUploadCallbackAboveL = null
                    }

                } else if (mUploadMessage != null) {
                    if (resultCode == Activity.RESULT_OK) {
                        mUploadMessage!!.onReceiveValue(result)
                        mUploadMessage = null
                    } else {
                        mUploadMessage!!.onReceiveValue(Uri.EMPTY)
                        mUploadMessage = null
                    }

                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != PHOTO_REQUEST || mUploadCallbackAboveL == null) {
            return
        }
        try {
            var results: Array<Uri>? = null
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    results = imageUri?.let { arrayOf(it) }
                } else {
                    val dataString = data.dataString
                    val clipData = data.clipData
                    if (clipData != null) {
                        results = arrayOf()
                        for (i in 0 until clipData.itemCount) {
                            val item = clipData.getItemAt(i)
                            results[i] = item.uri
                        }
                    }

                    if (dataString != null)
                        results = arrayOf(Uri.parse(dataString))
                }
            }
            mUploadCallbackAboveL!!.onReceiveValue(results)
            mUploadCallbackAboveL = null
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }

}


