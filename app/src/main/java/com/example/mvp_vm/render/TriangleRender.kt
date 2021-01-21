package com.example.mvp_vm.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @Description: java类作用描述
 * @Author: 刘宇飞 三角形render
 * @Date: 2021/1/19 17:14
 */
class TriangleRender : BaseRenderer(), GLSurfaceView.Renderer {
    private val TAG = javaClass.name


    private val vertexShaderCode = "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = vPosition;" +
            "}"

    private val fragmentShaderCode = "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}"

    private val triangleCoords = floatArrayOf(
        0.0f, 0.5f, 0.0f,  // top
        -0.5f, -0.5f, 0.0f,  // bottom left
        0.5f, -0.5f, 0.0f // bottom right
    )

    private lateinit var vertexBuffer: FloatBuffer
    private var mProgram: Int = 0

    //设置颜色，依次为红绿蓝和透明通道
    var color = floatArrayOf(1.0f, 0f, 0f, 1.0f)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.e(TAG, "onSurfaceCreated")
        //将背景设置为灰色
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        // 申请顶层需要的空间
        val bb: ByteBuffer = ByteBuffer.allocateDirect(triangleCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        // 将申请的存储空间数据转换成FloatBuffer 以便传入OpenGL程序
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(triangleCoords)
        vertexBuffer.position(0)
        // 加载顶点shader和片段shader
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        // 创建一个空的OpenGL程序
        mProgram = GLES20.glCreateProgram()
        // 分别将顶点shader和片段shader加载程序
        GLES20.glAttachShader(mProgram, vertexShader)
        GLES20.glAttachShader(mProgram, fragmentShader)
        // 将顶点着色器和片段着色器链接起来
        GLES20.glLinkProgram(mProgram)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.e(TAG, "onSurfaceChanged：width：$width height:$height")
        // 渲染窗口大小发生改变或者屏幕方法发生变化时候回调
        GLES20.glViewport(0, 0, width, height)

    }

    override fun onDrawFrame(gl: GL10?) {
        Log.e(TAG, "onDrawFrame")
        // 使用之前创建的程序
        GLES20.glUseProgram(mProgram)
        // 获取顶点字柄
        val positionLocation = GLES20.glGetAttribLocation(mProgram, "vPosition")
        // 启用顶点
        GLES20.glEnableVertexAttribArray(positionLocation)
        // 将顶点数据加载到顶点shader
        GLES20.glVertexAttribPointer(
            positionLocation,
            3,
            GLES20.GL_FLOAT,
            false,
            3 * 4,
            vertexBuffer
        )
        // 获取片段字柄
        val colorLocation = GLES20.glGetUniformLocation(mProgram, "vColor")
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(colorLocation, 1, color, 0)
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
        // 禁用顶点
        GLES20.glDisableVertexAttribArray(positionLocation)


    }


}