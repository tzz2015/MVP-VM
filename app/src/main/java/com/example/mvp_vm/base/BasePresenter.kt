package com.example.mvp_vm.base

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import java.lang.ref.WeakReference

/**
16 * @ClassName: BasePresenter
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-05-27 16:46
20 */
abstract class BasePresenter<V : BaseView, VM : BaseViewModel>(
    mView: V?,
    mViewModel: VM?,
    owner: LifecycleOwner
) {
    private var mViewRef: WeakReference<V>? = WeakReference<V>(mView)
    private var mViewModelRef: WeakReference<VM>? = WeakReference<VM>(mViewModel)

    init {
        this.initCommon(owner)
        this.start(owner)
    }

    /**
     * 获取View
     */
    fun getView(): V? {
        return mViewRef?.get()
    }

    /**
     * 获取ViewModel
     */
    fun getViewModel(): VM? {
        return mViewModelRef?.get()
    }


    /**
     * 统一显示加载框
     */
    private fun initCommon(owner: LifecycleOwner) {
        getViewModel()?.loadLiveData?.observe(owner, Observer {
            when (it) {
                true -> {
                    getView()?.showLoading()
                }
                else -> {
                    getView()?.hideLoading()
                }
            }
        })
    }

    protected abstract fun start(owner: LifecycleOwner)

    fun onDestroy() {
        mViewRef?.clear()
        mViewModelRef?.clear()
        mViewRef = null
        mViewModelRef = null
    }
}


