package com.example.mvp_vm.base

import android.arch.lifecycle.LifecycleOwner
import android.support.v4.app.FragmentActivity
import com.kaopiz.kprogresshud.KProgressHUD

/**
16 * @ClassName: BaseActivity
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-06-08 09:30
20 */
abstract class BaseActivity: FragmentActivity(),BaseView {
    private val mDialog by lazy { KProgressHUD.create(this).setLabel("加载中").setCancellable(true).setSize(160, 120) }
    override fun hideLoading() {
        mDialog.dismiss()
    }

    override fun showLoading() {
        mDialog.show()
    }

    fun getLifecycleOwner(): LifecycleOwner {
        return this
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoading()
    }
}