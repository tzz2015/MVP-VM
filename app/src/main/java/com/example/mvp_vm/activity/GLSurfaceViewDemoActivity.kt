package com.example.mvp_vm.activity

import android.opengl.GLSurfaceView
import android.os.Bundle
import com.example.mvp_vm.R
import com.example.mvp_vm.base.BaseActivity
import com.example.mvp_vm.render.TriangleRender
import kotlinx.android.synthetic.main.activity_gl_surface_view.*

class GLSurfaceViewDemoActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gl_surface_view)
        initSurfaceView()
    }

    /**
     * 进行初始化
     */
    private fun initSurfaceView() {
        // 设置OPenGL2.0
        gl_surfaceview.setEGLContextClientVersion(2)
        // 设置渲染器
        gl_surfaceview.setRenderer(TriangleRender())
        // 渲染方式，RENDERMODE_WHEN_DIRTY表示被动渲染，只有在调用requestRender或者onResume等方法时才会进行渲染。RENDERMODE_CONTINUOUSLY表示持续渲染*/
        gl_surfaceview.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }


    override fun onResume() {
        super.onResume()
        gl_surfaceview.onResume()
    }

    override fun onPause() {
        super.onPause()
        gl_surfaceview.onPause()
    }
}