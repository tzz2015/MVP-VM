package com.example.mvp_vm

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.mvp_vm.databinding.ActivityMainBinding
import com.kaopiz.kprogresshud.KProgressHUD

class MainActivity : AppCompatActivity(), HomeView {


    private lateinit var mBinding: ActivityMainBinding
    private val mViewModel by lazy { ViewModelProviders.of(this).get(HomeViewModel::class.java) }
    private val mPresenter by lazy { HomePresenter(this, mViewModel, this) }
    private val mDialog by lazy { KProgressHUD.create(this).setLabel("加载中").setCancellable(true).setSize(160, 120) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.lifecycleOwner = this
        mBinding.mViewModel = mViewModel
        mBinding.mPresenter = mPresenter


    }

    override fun hideLoading() {
        mDialog.dismiss()
    }

    override fun showLoading() {
        mDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoading()
        mPresenter.onDestroy()
    }

}
