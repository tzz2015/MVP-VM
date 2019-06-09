package com.example.mvp_vm

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.util.Log
import android.view.View
import com.example.mvp_vm.base.BasePresenter

/**
16 * @ClassName: HomePresenter
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-05-27 23:46
20 */
class HomePresenter(mContext: MainActivity, mView: HomeView) :
    BasePresenter<MainActivity, HomeView>(mContext, mView) {
    val mHomeModel by lazy { ViewModelProviders.of(mContext).get(HomeViewModel::class.java) }
    init {
        initCommon(mHomeModel)
        mHomeModel.textLiveData.observe(mContext, Observer {
            it?.let { data ->
                Log.e("HomePresenter", data.text)
            }
        })
    }


    /**
     * 点击按钮
     */
    fun clickChange() {
        mHomeModel.getTextData()
    }
    /**
     * 绑定底部view 底部布局逻辑由BottomPresenter去完成
     */
    fun binBottom(view:View){
        getContext()?.let { BottomPresenter(it,getView(),view) }
    }
}