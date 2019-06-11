package com.example.mvp_vm.presenter

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.util.Log
import android.view.View
import com.example.mvp_vm.MainActivity
import com.example.mvp_vm.WebActivity
import com.example.mvp_vm.base.BasePresenter
import com.example.mvp_vm.view.HomeView
import com.example.mvp_vm.viewmodel.HomeViewModel


/**
16 * @ClassName: HomePresenter
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-05-27 23:46
20 */
class HomePresenter(mContext: MainActivity, mView: HomeView) :
    BasePresenter<MainActivity, HomeView>(mContext, mView) {
    val mHomeModel by lazy { ViewModelProviders.of(mContext).get(HomeViewModel::class.java) }
    private var mBottomPresenter: BottomPresenter? = null

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
     * 跳转webView
     */
    fun toWeb() {
         val intent= Intent(getContext(), WebActivity::class.java)
         getContext()?.startActivity(intent)
       /* val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fubei://platformapi/startapp?appId=index&action=mine"))
        getContext()?.startActivity(intent)*/
    }

    /**
     * 绑定底部view 底部布局逻辑由BottomPresenter去完成
     */
    fun binBottom(view: View) {
        getContext()?.let {
            mBottomPresenter = BottomPresenter(it, getView(), view)
        }
    }

    override fun onDestroy() {
        mBottomPresenter?.onDestroy()
    }


}