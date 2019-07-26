package com.example.mvp_vm.base

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log

/**
16 * @ClassName: BaseViewModel
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-05-27 16:43
20 */
abstract class BaseViewModel : ViewModel() {
    val loadLiveData by lazy { MutableLiveData<Boolean>() }

    private var clearedListener: ViewModelClearedListener? = null

    fun setClearedListener(clearedListener: ViewModelClearedListener) {
        this.clearedListener = clearedListener
    }

    public override fun onCleared() {
        super.onCleared()
        clearedListener?.onCleared()
        Log.e("BaseViewModel", "onCleared")
    }

    interface ViewModelClearedListener {
        fun onCleared()
    }

}