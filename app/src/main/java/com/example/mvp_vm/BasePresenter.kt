package com.example.mvp_vm

import android.arch.lifecycle.LifecycleOwner

/**
16 * @ClassName: BasePresenter
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-05-27 16:46
20 */
abstract class BasePresenter<V : BaseView, VM : BaseViewModel>(
    var mView: V?,
    var mViewModel: VM?,
    var owner: LifecycleOwner
) {
    init {
        this.start(owner)
    }

    protected abstract fun start(owner: LifecycleOwner)
    fun onDestroy() {
        mView = null
        mViewModel?.onCleared()
        mViewModel = null
    }
}


