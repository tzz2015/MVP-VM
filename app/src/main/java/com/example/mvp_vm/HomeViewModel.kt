package com.example.mvp_vm

import android.arch.lifecycle.MutableLiveData
import android.os.SystemClock
import android.provider.Browser.sendString
import android.provider.Contacts
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Unconfined
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
    val loadLiveData by lazy { MutableLiveData<Boolean>() }


    /**
     * 模拟获取网络数据
     */
    /*fun getTextData() {
        loadLiveData.value = true
        SystemClock.sleep(1000)
        val text = TextModel("来了,老弟" + random.nextInt(100))
        textLiveData.value = text
        loadLiveData.value = false
    }*/
    fun getTextData()= runBlocking {
        GlobalScope.launch(Dispatchers.Main){
            loadLiveData.value = true
            delay(1000)
            val text = TextModel("来了,老弟" + random.nextInt(100))
            textLiveData.value = text
            loadLiveData.value = false
        }
    }
}