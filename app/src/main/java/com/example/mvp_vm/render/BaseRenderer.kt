package com.example.mvp_vm.render

import android.opengl.GLES20


/**
 * @Description: java类作用描述
 * @Author: 刘宇飞
 * @Date: 2021/1/19 17:17
 */
open class BaseRenderer {

    fun loadShader(type: Int, shaderCode: String?): Int {
        //根据type创建顶点着色器或者片元着色器
        val shader = GLES20.glCreateShader(type)
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}