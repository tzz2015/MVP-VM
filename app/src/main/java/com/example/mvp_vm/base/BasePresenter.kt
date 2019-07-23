package com.example.mvp_vm.base

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.util.Log

/**
16 * @ClassName: BasePresenter
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-05-27 16:46
20 */
abstract class BasePresenter<MC : BaseActivity, V : BaseView?>( var mContext: MC?,  var mView: V?) {

    protected fun <T : BaseViewModel> vmProviders(modelClass: Class<T>): T? {
        val viewModel = mContext?.let { ViewModelProviders.of(it).get(modelClass) }
        viewModel?.setClearedListener(object :BaseViewModel.ViewModelClearedListener{
            override fun onCleared() {
                onDestroy()
            }
        })
        viewModel?.let { initCommon(it) }
        return viewModel
    }





    /**
     * 统一显示加载框
     */
    private fun initCommon(viewModel: BaseViewModel) {
        mContext?.getLifecycleOwner()?.let { it ->
            viewModel.loadLiveData.observe(it, Observer {
                when (it) {
                    true -> {
                        mContext?.showLoading()
                    }
                    else -> {
                        mContext?.hideLoading()
                    }
                }
            })
        }

    }


    open fun onDestroy() {
        mContext=null
        mView=null
        Log.e("BasePresenter", "onDestroy")

    }
}


