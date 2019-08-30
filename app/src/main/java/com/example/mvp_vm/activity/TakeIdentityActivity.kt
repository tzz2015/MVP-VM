package com.example.mvp_vm.activity

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import com.example.mvp_vm.R
import com.example.mvp_vm.base.BaseActivity
import com.example.mvp_vm.utils.BitmapUtil
import com.example.mvp_vm.utils.StatusBarUtils
import kotlinx.android.synthetic.main.activity_take_identity.*
import kotlinx.coroutines.*
import java.io.File

@Suppress("PLUGIN_WARNING")
class TakeIdentityActivity : BaseActivity() {
    val TAG: String = "TakeIdentityActivity"
    private var mOutFileUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_identity)
        StatusBarUtils.hideNavigation(this)
        StatusBarUtils.hideStatusLan(this)
        StatusBarUtils.setStatusBarBackgroundColor(this, ContextCompat.getColor(this, R.color.common_transparent))
        initView()
    }

    private fun initView() {
        val outFile = File(Environment.getExternalStorageDirectory().path + File.separator, "temp_clip_image.jpg")
        if (outFile.exists()) {
            outFile.delete()
        }
        mOutFileUri = Uri.fromFile(outFile)
        rotationTextView(notice_correct_ll, false)
        initCameraLayoutParams()
        // 对焦
        camera_preview?.setOnClickListener {
            camera_preview?.focus()
        }
        // 拍照
        iv_take_photo?.setOnClickListener {
            takePhoto()
        }
        cancel_tv?.setOnClickListener { finish() }

    }

    /**
     * 重新设置拍摄预览的尺寸 解决拉伸问题
     */
    private fun initCameraLayoutParams() {
        view_take?.post {
            //获取屏幕最小边，设置为cameraPreview较窄的一边
            var screenMinSize = Math.min(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
            //根据screenMinSize，计算出cameraPreview的较宽的一边，长宽比为标准的16:9
            val top = view_take?.top!!.toFloat()
            var calculationHeight = screenMinSize / 9.0f * 16.0f
            if (top > calculationHeight) {
                calculationHeight = top
                screenMinSize = (top / 16.0f * 9.0f).toInt()
            }
            val layoutParams = ConstraintLayout.LayoutParams(screenMinSize, calculationHeight.toInt())
            camera_preview?.layoutParams = layoutParams
        }

    }

    /**
     * 拍照
     */
    private fun takePhoto() {
        camera_preview?.isEnabled = false
        camera_preview?.takePhoto { data, camera ->
            run {
                camera.stopPreview()
                saveAsyncExit(data)
            }
        }
    }

    /**
     * 保存图片并退出
     */
    private fun saveAsyncExit(data: ByteArray) = runBlocking {
        val bitmapTask = async(Dispatchers.IO) {
            return@async getBitmapData(data)
        }
        val bitmap = bitmapTask.await()
        bitmap?.let {
            val saveTask = async(Dispatchers.IO) {
                BitmapUtil.saveBitmap(bitmap, mOutFileUri)
            }
            saveTask.await()
            setResult(Activity.RESULT_OK)
            finish()
        }

    }

    /**
     * 获取bitmap
     */
    private fun getBitmapData(data: ByteArray): Bitmap? {
        var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        if (bitmap != null) {
            try {
                if (bitmap.width > bitmap.height) {
                    //处理手机拍出来的图片旋转了
                    bitmap = BitmapUtil.rotateBitmapByDegree(bitmap, 90)
                }
                val scale: Float = resources.displayMetrics.widthPixels.toFloat() / 2 / bitmap.width
                // 缩放图片 减少内存
                bitmap = BitmapUtil.compressBitmapByScale(bitmap, scale)
                val resultBitmap = curBitMap(bitmap)
                bitmap.recycle()
                return resultBitmap
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * 裁剪框内的图片
     */
    private fun curBitMap(bitmap: Bitmap): Bitmap {
        val widthScale = bitmap.width.toFloat() / camera_preview?.width!!.toFloat()
        val heightScale = bitmap.height.toFloat() / camera_preview?.height!!.toFloat()
        val curBitmap = Bitmap.createBitmap(
            bitmap,
            (clip_frame_iv.left * widthScale).toInt(),
            (clip_frame_iv.top * heightScale).toInt(),
            ((clip_frame_iv.right - clip_frame_iv.left) * widthScale).toInt(),
            ((clip_frame_iv.bottom - clip_frame_iv.top) * heightScale).toInt()
        )
        return BitmapUtil.rotateBitmapByDegree(curBitmap, 270)
    }


    /**
     * 旋转View
     */
    private fun rotationTextView(view: View, transRight: Boolean) {
        view.post {
            val measuredWidth = view.measuredWidth
            view.rotation = 90f
            if (transRight) {
                view.translationX = measuredWidth / 2f
            } else {
                view.translationX = -measuredWidth / 2f
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
