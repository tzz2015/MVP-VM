package com.example.mvp_vm

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.util.Log

/**
16 * @ClassName: HomePresenter
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-05-27 23:46
20 */
class HomePresenter(mView: HomeView, mViewModel: HomeViewModel, owner: LifecycleOwner) :
    BasePresenter<HomeView, HomeViewModel>(mView, mViewModel, owner) {
    override fun start(owner: LifecycleOwner) {
        mViewModel?.textLiveData!!.observe(owner, Observer {
            it?.let { data ->
                Log.e("HomePresenter", data.text)
            }
        })
    }

    /**
     * 点击按钮
     */
    fun clickChange() {
        mViewModel?.getTextData()
    }
}