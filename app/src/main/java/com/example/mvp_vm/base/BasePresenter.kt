package com.example.mvp_vm.base

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.example.mvp_vm.App

/**
16 * @ClassName: BasePresenter
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-05-27 16:46
20 */
abstract class BasePresenter<MC : Context, V : BaseView?>(var mContext: MC?, var mView: V?) {
    /**
     * 生成viewModel
     */
    protected fun <T : BaseViewModel> vmProviders(modelClass: Class<T>): T {
        val viewModel: BaseViewModel
        if (mContext is FragmentActivity || mContext is Fragment) {
            viewModel = if (mContext is BaseActivity) {
                ViewModelProviders.of(mContext as FragmentActivity).get(modelClass)
            } else {
                ViewModelProviders.of(mContext as Fragment).get(modelClass)
            }
        } else {
            viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(App.getInstance()).create(modelClass)
        }
        // 监听viewModel被销毁时 也销毁presenter
        viewModel.setClearedListener(object : BaseViewModel.ViewModelClearedListener {
            override fun onCleared() {
                onDestroy()
            }
        })
        initCommon(viewModel)
        return viewModel
    }


    /**
     * 统一显示加载框
     */
    private fun initCommon(viewModel: BaseViewModel) {
        if (mContext is BaseActivity || isBaseActivity()) {
            val activity = mContext as BaseActivity
            viewModel.loadLiveData.observe(activity.getLifecycleOwner(), Observer {
                when (it) {
                    true -> {
                        mView?.showLoading()
                    }
                    else -> {
                        mView?.hideLoading()
                    }
                }
            })
        }
    }

    /**
     * 判断是否为BaseActivity
     */
    private fun isBaseActivity(): Boolean {
        if (mContext is Fragment) {
            val fragment = mContext as Fragment
            if (fragment.activity is BaseActivity) {
                return true
            }
        }
        return false
    }


    open fun onDestroy() {
        mContext = null
        mView = null
        Log.e("BasePresenter", "onDestroy")

    }
}


