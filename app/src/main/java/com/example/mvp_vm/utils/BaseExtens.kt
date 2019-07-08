package com.example.mvp_vm.utils

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.widget.Toast
import java.util.*


/**
 * 页面描述：一些扩展
 *
 */
private var toast: Toast? = null

fun Context.getCompactColor(@ColorRes colorRes: Int): Int = ContextCompat.getColor(this, colorRes)

fun Context.toast(msg: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
	toast ?: let {
		toast = Toast.makeText(this.applicationContext, null, duration)
	}
	toast?.apply {
		setText(msg)
		show()
	}
}

@SuppressLint("ShowToast")
fun <T : Fragment> T.toast(text: CharSequence) {
	context?.toast(text)
}

/**
 * @param resId 字符串资源
 */
fun <T : Fragment> T.toast(@StringRes resId: Int) {
	toast(getString(resId))
}


/**
 * 比较内容是否一致
 */
infix fun Any.sameAs(other: Any) = this == other

/**
 * 随机数
 */
fun ClosedRange<Int>.random() = Random().nextInt((endInclusive + 1) - start) + start

/**
 * 手机号码带空格显示
 */
fun String.formatPhone() = "${this.substring(0, 3)} ${"****"} ${this.substring(7, 11)}"