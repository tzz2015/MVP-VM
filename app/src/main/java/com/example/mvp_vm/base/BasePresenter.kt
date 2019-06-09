package com.example.mvp_vm.base

import android.app.Activity
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.support.v4.app.FragmentActivity
import java.lang.ref.WeakReference

/**
16 * @ClassName: BasePresenter
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-05-27 16:46
20 */
abstract class BasePresenter< MC : BaseActivity,V : BaseView?>(mContext: MC, mView: V?) {
    private  val mViewRef: WeakReference<V?> = WeakReference(mView)
    private  val mContextRef: WeakReference<MC> = WeakReference(mContext)


    fun getContext():MC?{
        return mContextRef.get()
    }

    /**
     * 获取View
     */
    fun getView(): V? {
        return mViewRef.get()
    }



    /**
     * 统一显示加载框
     */
    fun initCommon(viewModel: BaseViewModel) {
        viewModel.loadLiveData.observe(mContextRef.get()!!.getLifecycleOwner(), Observer {
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


    open fun onDestroy() {
        mViewRef.clear()
        mContextRef.clear()
    }
}


