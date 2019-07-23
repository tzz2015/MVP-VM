package com.example.mvp_vm.presenter

import android.arch.lifecycle.Observer
import android.content.Intent
import android.util.Log
import android.view.View
import com.example.mvp_vm.MainActivity
import com.example.mvp_vm.activity.CutImgActivity
import com.example.mvp_vm.activity.DoubleScrollActivity
import com.example.mvp_vm.activity.TakeIdentityActivity
import com.example.mvp_vm.activity.WebActivity
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
    val mHomeModel by lazy { vmProviders(HomeViewModel::class.java) }
    private var mBottomPresenter: BottomPresenter? = null

    init {
        mHomeModel?.textLiveData!!.observe(mContext, Observer {
            it?.let { data ->
                Log.e("HomePresenter", data.text)
            }
        })
    }


    /**
     * 点击按钮
     */
    fun clickChange() {
        mHomeModel?.getTextData()
    }

    /**
     * 跳转webView
     */
    fun toWeb() {
        val intent = Intent(mContext, WebActivity::class.java)
        mContext?.startActivity(intent)
    }

    /**
     * 跳转裁剪界面
     */
    fun toCutActivity() {
        val intent = Intent(mContext, CutImgActivity::class.java)
        mContext?.startActivityForResult(intent, 1)
    }

    /**
     * 跳转到双scrollView的activity
     */
    fun toDoubleScrollView() {
        val intent = Intent(mContext, DoubleScrollActivity::class.java)
        mContext?.startActivity(intent)
    }

    /**
     * 身份证拍照
     */
    fun toTakeIdentity() {
        val intent = Intent(mContext, TakeIdentityActivity::class.java)
        mContext?.startActivityForResult(intent, 1)
    }


    /**
     * 绑定底部view 底部布局逻辑由BottomPresenter去完成
     */
    fun binBottom(view: View) {
        mContext?.let {
            mBottomPresenter = BottomPresenter(it, mView, view)
        }
    }

}