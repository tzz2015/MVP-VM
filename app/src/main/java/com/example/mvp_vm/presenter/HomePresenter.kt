package com.example.mvp_vm.presenter

import android.arch.lifecycle.Observer
import android.content.Intent
import android.util.Log
import android.view.View
import com.example.mvp_vm.activity.CutImgActivity
import com.example.mvp_vm.activity.DoubleScrollActivity
import com.example.mvp_vm.activity.TakeIdentityActivity
import com.example.mvp_vm.activity.WebActivity
import com.example.mvp_vm.base.BaseActivity
import com.example.mvp_vm.base.BasePresenter
import com.example.mvp_vm.contact.HomeContact
import com.example.mvp_vm.viewmodel.HomeViewModel


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
     * 绑定底部view 底部布局逻辑由BottomPresenter去完成
     */
    override fun binBottom(view: View) {
        mContext?.let {
            mBottomPresenter = BottomPresenter(it, mView, view)
        }
    }

}