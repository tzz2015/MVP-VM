package com.example.mvp_vm.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.example.mvp_vm.R
import com.example.mvp_vm.base.BaseActivity
import com.example.mvp_vm.utils.StatusBarUtils
import com.example.mvp_vm.utils.statusbar.StatusBarFontHelper

class DoubleScrollActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_double_scroll)
        StatusBarUtils.hideStatusLan(this)
        StatusBarUtils.setStatusBarBackgroundColor(this, ContextCompat.getColor(this, R.color.common_transparent))
        StatusBarFontHelper.setStatusBarMode(this, true)

    }
}
