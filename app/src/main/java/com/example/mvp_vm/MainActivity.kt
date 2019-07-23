package com.example.mvp_vm

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.example.mvp_vm.base.BaseActivity
import com.example.mvp_vm.databinding.ActivityMainBinding
import com.example.mvp_vm.presenter.HomePresenter
import com.example.mvp_vm.view.HomeView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileInputStream

class MainActivity : BaseActivity(), HomeView {


    private lateinit var mBinding: ActivityMainBinding
    private val mPresenter by lazy { HomePresenter(this, this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.lifecycleOwner = this
        mBinding.mPresenter = mPresenter
        mBinding.mViewModel = mPresenter.mHomeModel
        // 底部布局逻辑由底部的presenter实现
        mPresenter.binBottom(mBinding.inBottom.root)
        initView()
    }

    private fun initView() {
        val outFile = File(Environment.getExternalStorageDirectory().path + File.separator, "temp_clip_image.jpg")
        if (outFile.exists()) {
            outFile.delete()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val outFile = File(Environment.getExternalStorageDirectory().path + File.separator, "temp_clip_image.jpg")
        if (outFile.exists()) {
            try {
                val fileInputStream = FileInputStream(outFile)
                val bitmap = BitmapFactory.decodeStream(fileInputStream)
                iv_show.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Log.e("MainActivity", e.message)
            }
        }
    }

}
