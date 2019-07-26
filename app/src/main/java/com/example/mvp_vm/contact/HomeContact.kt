package com.example.mvp_vm.contact

import com.example.mvp_vm.base.BaseView

/**
 *
 * @author lyf
 * @version HomeContact.java, v 0.1 2019-07-24 11:31 lyf
 */
interface HomeContact {
    interface View : BaseView

    interface Presenter {
        fun clickChange()
        fun toWeb()
        fun toCutActivity()
        fun toDoubleScrollView()
        fun toTakeIdentity()
        fun binBottom(view: android.view.View)
    }

}