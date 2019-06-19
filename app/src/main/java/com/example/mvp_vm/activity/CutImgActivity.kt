package com.example.mvp_vm.activity

import android.os.Bundle
import android.util.Log
import com.example.mvp_vm.R
import com.example.mvp_vm.base.BaseActivity
import com.example.mvp_vm.utils.StatusBarUtils
import kotlinx.android.synthetic.main.activity_cut_img.*

class CutImgActivity : BaseActivity() {
    private val TAG = CutImgActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cut_img)
        StatusBarUtils.setStatusBarBackgroundColor(this, "#000000")
        toolbar_title_tv.text = "裁剪"
        drop_zv.setCheckBorder(false)
        /**
         * 缩放监听
         */
        scanner_sv.setScaleListener { sx, sy, px, py ->

            Log.e(TAG, "scale:$sx--x:$px--Y:$py")
            drop_zv.setScale(sx, sy, px, py)

        }


    }

}
