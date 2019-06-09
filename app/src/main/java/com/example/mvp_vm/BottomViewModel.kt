package com.example.mvp_vm

import android.arch.lifecycle.MutableLiveData
import com.example.mvp_vm.base.BaseViewModel
import kotlinx.coroutines.*
import kotlin.random.Random

/**
16 * @ClassName: BottomViewModel
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-06-09 11:57
20 */
class BottomViewModel : BaseViewModel() {
    private val random by lazy { java.util.Random() }
    val colorLiveData by lazy { MutableLiveData<TextModel>() }

    /**
     * 模拟获取网络数据
     */
    fun getColorData() = runBlocking {
        GlobalScope.launch(Dispatchers.Main) {
            loadLiveData.value = true
            delay(1000)
            val text = TextModel(randomColor(6))
            colorLiveData.value = text
            loadLiveData.value = false
        }
    }

    /**
     * 获取随机颜色
     */
    private fun randomColor(len: Int): String {
        return try {
            val result = StringBuffer()
            for (i in 0 until len) {
                //随机生成0-15的数值并转换成16进制
                result.append(Integer.toHexString(random.nextInt(16)))
            }
            "#"+result.toString().toUpperCase()
        } catch (e: Exception) {
            "#00CCCC"
        }

    }


}