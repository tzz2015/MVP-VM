package com.example.mvp_vm.activity

import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.example.mvp_vm.R
import com.example.mvp_vm.base.BaseActivity
import com.example.mvp_vm.utils.StatusBarUtils
import com.example.mvp_vm.utils.Utils
import kotlinx.android.synthetic.main.activity_cut_img.*
import java.io.File

class CutImgActivity : BaseActivity() {
    private val TAG = CutImgActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cut_img)
        StatusBarUtils.setStatusBarBackgroundColor(this, "#000000")
        toolbar_title_tv.text = "裁剪"
        drop_zv.setCheckBorder(false)
//        scanner_sv.setAspectRatio(0.8f)
        toolbar.setOnClickListener { finish() }
        //缩放监听
        scanner_sv.setScaleListener { sx, sy, px, py ->
            Log.e(TAG, "scaleX:$sx--scaleY:$sy--x:$px--Y:$py")
            drop_zv.setScale(sx, sy, px, py)
            drop_zv.tryTranslateXy(0.5f)
        }
        finish_tv.setOnClickListener {
            saveBitmap()
        }
        drop_zv.binScannerView(scanner_sv)


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
            Utils.saveBitmap(bitmap, outFile.path)
            setResult(1)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            drop_zv.isDrawingCacheEnabled = false

        }
    }




}
