package com.example.mvp_vm.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.Log
import com.example.mvp_vm.R
import com.example.mvp_vm.base.BaseActivity
import com.example.mvp_vm.utils.StatusBarUtils
import com.example.mvp_vm.utils.Utils
import kotlinx.android.synthetic.main.activity_take_identity.*
import java.io.File

class TakeIdentityActivity : BaseActivity() {
    val TAG: String = "TakeIdentityActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_identity)
        StatusBarUtils.hideStatusLan(this)
        StatusBarUtils.setStatusBarBackgroundColor(this, ContextCompat.getColor(this, R.color.common_transparent))
        rotationTextView()
        initView()
    }

    private fun initView() {
        camera_preview?.setOnClickListener {
            camera_preview?.focus()
        }
        iv_take_photo?.setOnClickListener {
            camera_preview?.isEnabled = false
            takePhoto()
        }

    }

    private fun takePhoto() {
        camera_preview?.takePhoto { data, camera ->
            run {
                camera.stopPreview()
                Thread(Runnable {
                    var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                    if (bitmap != null) {
                        if (bitmap.width > bitmap.height) {
                            //处理手机拍出来的图片旋转了
                            bitmap = rotateBitmapByDegree(bitmap, 90)
                        }
                        bitmap = curBitMap(bitmap)
                        saveToLocal(bitmap)
                    }

                }).start()
            }
        }

    }

    private fun curBitMap(bitmap: Bitmap): Bitmap {
        val widthScale = bitmap.width.toFloat() / camera_preview?.width!!.toFloat()
        val heightScale = bitmap.height.toFloat() / camera_preview?.height!!.toFloat()


        val curBitmap = Bitmap.createBitmap(
            bitmap,
            (iv_clip_frame.left * widthScale).toInt(),
            (iv_clip_frame.top * heightScale).toInt(),
            ((iv_clip_frame.right - iv_clip_frame.left) * widthScale).toInt(),
            ((iv_clip_frame.bottom - iv_clip_frame.top) * heightScale).toInt()
        )
        return rotateBitmapByDegree(curBitmap, 270)
    }

    private fun saveToLocal(bitmap: Bitmap?) {
        val outFile =
            File(Environment.getExternalStorageDirectory().path + File.separator, "temp_clip_image.jpg")
        if (!outFile.exists()) {
            outFile.mkdir()
        } else {
            outFile.delete()
        }
        Utils.saveBitmap(bitmap, outFile.path)
        Log.e(TAG, "执行完成")
        runOnUiThread {
            setResult(1)
            finish()
            Log.e(TAG, "销毁页面")
        }
    }

    /**
     * 旋转TextView
     */
    private fun rotationTextView() {
        tv_left?.post {
            val leftWidth = tv_left?.measuredWidth
            val rightWidth = tv_right?.measuredWidth
            tv_left?.rotation = 90f
            if (leftWidth != null) {
                tv_left?.translationX = leftWidth / 2f
            }
            tv_right?.rotation = 90f
            if (rightWidth != null) {
                tv_right?.translationX = -rightWidth / 2f
            }
        }
    }

    override fun onStart() {
        super.onStart()
        camera_preview?.onStart()
    }

    override fun onStop() {
        super.onStop()
        camera_preview?.onStop()
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm
     * 需要旋转的图片
     * @param degree
     * 旋转角度
     * @return 旋转后的图片
     */
    private fun rotateBitmapByDegree(bm: Bitmap, degree: Int): Bitmap {
        var returnBm: Bitmap? = null

        // 根据旋转角度，生成旋转矩阵
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(
                bm, 0, 0, bm.width,
                bm.height, matrix, true
            )
        } catch (e: OutOfMemoryError) {
        }

        if (returnBm == null) {
            returnBm = bm
        }
        if (bm != returnBm) {
            bm.recycle()
        }
        return returnBm
    }


}
