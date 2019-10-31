package com.example.mvp_vm.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.util.Log
import com.example.mvp_vm.base.BaseViewModel
import com.example.mvp_vm.model.TextModel
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.io.File
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


    fun uploadPhoto(context: Context, localUrl: List<String>) = runBlocking {
        loadLiveData.value = true
        // 上传
        uploadCompressPhoto(localUrl)
    }

    private fun uploadCompressPhoto(compressList: List<String>) {
        loadLiveData.value = true
        Observable.fromIterable(compressList)
            .concatMap { path ->
                Log.e("concatMap", path)
                sendOss(path)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(path: String) {
                    Log.e("上传完成：", path)
                }

                override fun onError(e: Throwable) {
                    loadLiveData.value = false
                    Log.e("onError：", e.message)
                }

                override fun onComplete() {
                    loadLiveData.value = false
                    Log.e("onComplete：", "@@@@@@@@@@@@@")
                }
            })

    }

    /**
     * 请求网络上传图片
     */
    private fun sendOss(path: String): Observable<String> {
        return Observable.create { emitter ->
            if(path!="d"){
                emitter.onNext("阿里云地址:$path")
                emitter.onComplete()
            }else{
//                emitter.onNext("阿里云地址:")
                emitter.onComplete()
            }

        }
    }
}


