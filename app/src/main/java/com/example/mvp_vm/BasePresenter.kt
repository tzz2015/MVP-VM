package com.example.mvp_vm

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer

/**
16 * @ClassName: BasePresenter
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-05-27 16:46
20 */
abstract class BasePresenter<V : BaseView, VM : BaseViewModel>(
    var mView: V?,
    var mViewModel: VM?,
    owner: LifecycleOwner
) {
    init {
        this.initCommon(owner)
        this.start(owner)
    }

    /**
     * 统一显示加载框
     */
    private fun initCommon(owner: LifecycleOwner) {
        mViewModel?.loadLiveData?.observe(owner, Observer {
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

    protected abstract fun start(owner: LifecycleOwner)

    fun onDestroy() {
        mView = null
        mViewModel?.onCleared()
        mViewModel = null
    }
}


