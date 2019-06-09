package com.example.mvp_vm

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.example.mvp_vm.base.BaseActivity
import com.example.mvp_vm.databinding.ActivityMainBinding

class MainActivity : BaseActivity(), HomeView {


    private lateinit var mBinding: ActivityMainBinding
    private val mPresenter by lazy { HomePresenter(this, this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.lifecycleOwner = this
        mBinding.mPresenter = mPresenter
        mBinding.mViewModel = mPresenter.mHomeModel
        // 底部布局逻辑由底部的presenter实现
        mPresenter.binBottom(mBinding.inBottom.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestroy()
    }

}
