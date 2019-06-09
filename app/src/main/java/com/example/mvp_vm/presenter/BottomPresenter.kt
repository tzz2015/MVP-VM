package com.example.mvp_vm.presenter

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.util.Log
import android.view.View
import com.example.mvp_vm.viewmodel.BottomViewModel
import com.example.mvp_vm.MainActivity
import com.example.mvp_vm.base.BasePresenter
import com.example.mvp_vm.databinding.LayoutBottomBinding
import com.example.mvp_vm.view.HomeView

/**
16 * @ClassName: BottomPresenter
17 * @Description: 底部布局presenter
18 * @Author: lyf
19 * @Date: 2019-06-09 11:56
20 */
class BottomPresenter(mContext: MainActivity, mIView: HomeView?, mView: View) :
    BasePresenter<MainActivity, HomeView>(mContext, mIView) {
    private val mBottomModel by lazy { ViewModelProviders.of(mContext).get(BottomViewModel::class.java) }

    private var mBinding: LayoutBottomBinding? = null

    init {
        mBinding = DataBindingUtil.bind(mView)
        mBinding?.mPresenter = this
        mBinding?.lifecycleOwner = mContext
        mBinding?.mViewModel = mBottomModel
        mBottomModel.colorLiveData.observe(mContext, Observer {
            it?.let { data ->
                Log.e("color:", ""+data.text)
            }
        })
        initCommon(mBottomModel)
    }

    /**
     * 点击切换颜色
     */
    fun changeColor() {
        mBottomModel.getColorData()
    }

}