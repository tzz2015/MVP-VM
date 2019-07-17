package com.example.mvp_vm.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.Log
import com.example.mvp_vm.R
import com.example.mvp_vm.base.BaseActivity
import com.example.mvp_vm.utils.StatusBarUtils
import com.example.mvp_vm.utils.Utils
import com.example.mvp_vm.utils.Utils.rotateBitmapByDegree
import kotlinx.android.synthetic.main.activity_take_identity.*
import kotlinx.coroutines.*
import java.io.File
import java.lang.Runnable

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
            takePhoto2()
        }
        initCameraLayoutParams()

    }

    private fun initCameraLayoutParams() {
        //获取屏幕最小边，设置为cameraPreview较窄的一边
        val screenMinSize = Math.min(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
        //根据screenMinSize，计算出cameraPreview的较宽的一边，长宽比为标准的16:9
        val maxSize = screenMinSize / 9.0f * 16.0f
        val layoutParams = ConstraintLayout.LayoutParams(screenMinSize, maxSize.toInt())
        camera_preview?.layoutParams = layoutParams
    }

    /**
     * 拍照
     */
    private fun takePhoto() {
        val startTime = System.currentTimeMillis()
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
                        val scale: Float = resources.displayMetrics.widthPixels.toFloat() / 2 / bitmap.width
                        // 缩放图片 减少内存
                        bitmap = Utils.compressBitmapByScale(bitmap, scale)
                        saveToLocal(curBitMap(bitmap))
                        val endTime = System.currentTimeMillis()
                        Log.e(TAG, "运行时间:" + (endTime - startTime))
                    }

                }).start()
            }
        }

    }

    /**
     * 拍照
     */
    private fun takePhoto2() {
        camera_preview?.takePhoto { data, camera ->
            run {
                camera.stopPreview()
                saveAsync(data)

            }
        }

    }

    private fun saveAsync(data: ByteArray) = runBlocking {
        val getBitmapTask = async(Dispatchers.IO) {
            var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            if (bitmap != null) {
                if (bitmap.width > bitmap.height) {
                    //处理手机拍出来的图片旋转了
                    bitmap = rotateBitmapByDegree(bitmap, 90)
                }
                val scale: Float = resources.displayMetrics.widthPixels.toFloat() / 2 / bitmap.width
                // 缩放图片 减少内存
                bitmap = Utils.compressBitmapByScale(bitmap, scale)
                val curBitMap = curBitMap(bitmap)
                bitmap.recycle()
                return@async curBitMap
            }
            return@async null
        }
        val bitmap = getBitmapTask.await()
        if(bitmap!=null){
            val saveTask = async(Dispatchers.IO) {
                val outFile =
                    File(Environment.getExternalStorageDirectory().path + File.separator, "temp_clip_image.jpg")
                if (!outFile.exists()) {
                    outFile.mkdir()
                } else {
                    outFile.delete()
                }
                Utils.saveBitmap(bitmap, outFile.path)
            }
            saveTask.await()
            setResult(1)
            finish()
            Log.e(TAG, "销毁页面")
        }

    }


    /**
     * 裁剪框内的图片
     */
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

    /**
     * 存储到本地
     */
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
     * 存储到本地
     */
    private fun saveToLocal2(bitmap: Bitmap?) {
        val outFile =
            File(Environment.getExternalStorageDirectory().path + File.separator, "temp_clip_image.jpg")
        if (!outFile.exists()) {
            outFile.mkdir()
        } else {
            outFile.delete()
        }
        Log.e(TAG, "执行完成")
        Utils.saveBitmap(bitmap, outFile.path)
        GlobalScope.launch(Dispatchers.Main) {
            //            Utils.saveBitmap(bitmap, outFile.path)
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


}
