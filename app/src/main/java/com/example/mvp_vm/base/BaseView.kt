package com.example.mvp_vm.base

/**
16 * @ClassName: BaseView
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-05-27 16:46
20 */
interface BaseView {

    /**
     * 显示网络请求loading
     */
    fun showLoading()

    /**
     * 隐藏网络请求loading
     */
    fun hideLoading()
}