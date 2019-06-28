package com.example.mvp_vm.activity

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.example.mvp_vm.R
import com.example.mvp_vm.base.BaseActivity
import com.example.mvp_vm.utils.StatusBarUtils
import kotlinx.android.synthetic.main.activity_cut_img.*
import java.io.File
import java.io.FileOutputStream
import java.net.URI

class CutImgActivity : BaseActivity() {
    private val TAG = CutImgActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cut_img)
        StatusBarUtils.setStatusBarBackgroundColor(this, "#000000")
        toolbar_title_tv.text = "裁剪"
        drop_zv.setCheckBorder(false)
        toolbar.setOnClickListener { finish() }
        //缩放监听
        scanner_sv.setScaleListener { sx, sy, px, py ->

            Log.e(TAG, "scaleX:$sx--scaleY:$sy--x:$px--Y:$py")
            drop_zv.setScale(sx, sy, px, py)
        }
        finish_tv.setOnClickListener {
            saveBitmap()
        }
        scanner_sv.setAspectRatio(0.8f)


    }

    /**
     * 获取截图
     */
    private fun saveBitmap() {
        try {
            drop_zv.isDrawingCacheEnabled = true
            val bitmap = scanner_sv.getBitmap(drop_zv)
            val outFile =
                File(Environment.getExternalStorageDirectory().path + File.separator, "temp_clip_image.jpg")
            if (!outFile.exists()) {
                outFile.mkdir()
            } else {
                outFile.delete()
            }
            saveBitmap(bitmap, Uri.fromFile(outFile))
            setResult(1)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            drop_zv.isDrawingCacheEnabled = false

        }
    }

    /**
     * 获取bitmap
     */
    fun saveBitmap(bitmap: Bitmap?, mOutFileUri: Uri) {
        var fileOutputStream: FileOutputStream? = null
        try {
            val outFile = File(URI(mOutFileUri.toString()))
            fileOutputStream = FileOutputStream(outFile)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            val width = bitmap.width
            val height = bitmap.height
            Log.e(TAG, "width:$width ----height:$height")
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bitmap?.recycle()
        }
    }


}
