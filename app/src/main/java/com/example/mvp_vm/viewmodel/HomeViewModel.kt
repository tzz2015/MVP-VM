package com.example.mvp_vm.viewmodel

import android.arch.lifecycle.MutableLiveData
import com.example.mvp_vm.base.BaseViewModel
import com.example.mvp_vm.model.TextModel
import kotlinx.coroutines.*
import java.util.*

/**
16 * @ClassName: HomeViewModel
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-05-27 23:45
20 */
class HomeViewModel : BaseViewModel() {
    private val random by lazy { Random() }

    val textLiveData by lazy { MutableLiveData<TextModel>() }


    /**
     * 模拟获取网络数据
     */
    fun getTextData() = runBlocking {
        GlobalScope.launch(Dispatchers.Main) {
            loadLiveData.value = true
            delay(1000)
            val text = TextModel("随机数字：" + random.nextInt(100))
            textLiveData.value = text
            loadLiveData.value = false
        }
    }
}