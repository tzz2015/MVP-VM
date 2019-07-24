package com.example.mvp_vm.presenter

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.util.Log
import android.view.View
import com.example.mvp_vm.base.BaseActivity
import com.example.mvp_vm.base.BasePresenter
import com.example.mvp_vm.databinding.LayoutBottomBinding
import com.example.mvp_vm.view.HomeView
import com.example.mvp_vm.viewmodel.BottomViewModel

/**
16 * @ClassName: BottomPresenter
17 * @Description: 底部布局presenter
18 * @Author: lyf
19 * @Date: 2019-06-09 11:56
20 */
class BottomPresenter(mContext: BaseActivity, mIView: HomeView?, mView: View) :
    BasePresenter<BaseActivity, HomeView>(mContext, mIView) {
    private val mBottomModel by lazy { vmProviders(BottomViewModel::class.java) }

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
    }

    /**
     * 点击切换颜色
     */
    fun changeColor() {
        mBottomModel.getColorData()
    }

}