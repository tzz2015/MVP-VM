package com.example.mvp_vm

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable

/**
16 * @ClassName: TextModel
17 * @Description: java类作用描述
18 * @Author: lyf
19 * @Date: 2019-05-28 00:12
20 */
data class TextModel(var text: String){
    fun getIntColor():Int{
        return Color.parseColor(text)
    }
}