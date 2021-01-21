package com.example.mvp_vm.presenter

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.example.mvp_vm.activity.*
import com.example.mvp_vm.base.BaseActivity
import com.example.mvp_vm.base.BasePresenter
import com.example.mvp_vm.contact.HomeContact
import com.example.mvp_vm.viewmodel.HomeViewModel
import io.reactivex.Flowable
import io.reactivex.functions.Function


/**
16 * @ClassName: HomePresenter
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-05-27 23:46
20 */
class HomePresenter(mContext: BaseActivity, mView: HomeContact.View) :
    BasePresenter<BaseActivity, HomeContact.View>(mContext, mView), HomeContact.Presenter {


    val mHomeModel by lazy { vmProviders(HomeViewModel::class.java) }
    private var mBottomPresenter: BottomPresenter? = null

    init {
        mHomeModel.textLiveData.observe(mContext, Observer {
            it?.let { data ->
                Log.e("HomePresenter", data.text)
            }
        })
    }


    /**
     * 点击按钮
     */
    override fun clickChange() {
        mHomeModel.getTextData()
    }

    /**
     * 跳转webView
     */
    override fun toWeb() {
        val intent = Intent(mContext, WebActivity::class.java)
        mContext?.startActivity(intent)
    }

    /**
     * 跳转裁剪界面
     */
    override fun toCutActivity() {
        val intent = Intent(mContext, CutImgActivity::class.java)
        mContext?.startActivityForResult(intent, 1)
    }

    /**
     * 跳转到双scrollView的activity
     */
    override fun toDoubleScrollView() {
        val intent = Intent(mContext, DoubleScrollActivity::class.java)
        mContext?.startActivity(intent)
    }

    /**
     * 身份证拍照
     */
    override fun toTakeIdentity() {
        val intent = Intent(mContext, TakeIdentityActivity::class.java)
        mContext?.startActivityForResult(intent, 1)
    }

    /**
     * GLSurfaceView
     */
    override fun toGLSurfaceView() {
        val intent = Intent(mContext, GLSurfaceViewDemoActivity::class.java)
        mContext?.startActivity(intent)
    }


    /**
     * 绑定底部view 底部布局逻辑由BottomPresenter去完成
     */
    override fun binBottom(view: View) {
        mContext?.let {
            mBottomPresenter = BottomPresenter(it, mView, view)
        }
    }

    override fun rxConcat() {
        val testList: MutableList<String> = ArrayList()
        testList.add("a")
        testList.add("b")
        testList.add("c")
        testList.add("d")
        testList.add("e")
        mHomeModel.uploadPhoto(mContext as Context, testList)
    }


    /**
     * scheme 测试工具
     */
    override fun schemeTest() {
        val url =
            "taobao://huodong.m.taobao.com/act/snipcode.html?_wml_code=VGFPUgSxvTlM1SzQFDGRM0j4iNmwi5WF%2BbLLtsHx6B5DiYq6zUdc4Ru9G6%2Fgmhbk8CYCPWOQsrfC7jEiHAlIo5l3z%2Fwpxsc8gG2MbvxT%2B9gi%2FGEQ4dYkZWzmZJb2SHM44L3sTUHxQoUnct2PokVWcvudwePNhCcqDohVJHOFiCc%3D"
        var data = UrlModel(url, false)

        val subscribe = Flowable
            .just(data)
            .flatMap(telFunction())
            .flatMap(localFunction())
            .subscribe { it ->
                it.let {
                    Log.e("HomePresenter", "是否包含:${it.deal}")
                }
            }
    }

    data class UrlModel(var url: String, var deal: Boolean)

    private fun telFunction(): Function<UrlModel, Flowable<UrlModel>> {
        return Function { it ->
            it.let {
                it.deal = it.url.startsWith("taobao://") || it.url.startsWith("tel://")
                return@Function Flowable.just(it)
            }
        }
    }

    private fun localFunction(): Function<UrlModel, Flowable<UrlModel>> {
        return Function { it ->
            it.let {
                Log.e("HomePresenter", "上一步已经处理:${it.deal}")
                return@Function Flowable.just(it)
            }
        }
    }

}


