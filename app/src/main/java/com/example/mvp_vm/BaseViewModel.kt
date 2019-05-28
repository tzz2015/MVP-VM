package com.example.mvp_vm

import android.arch.lifecycle.ViewModel
import android.util.Log

/**
16 * @ClassName: BaseViewModel
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-05-27 16:43
20 */
abstract class BaseViewModel : ViewModel() {
    public override fun onCleared() {
        super.onCleared()
        Log.e("BaseViewModel", "onCleared")
    }
}