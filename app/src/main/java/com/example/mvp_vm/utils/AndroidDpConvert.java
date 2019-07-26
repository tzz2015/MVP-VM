package com.example.mvp_vm.utils;


/**
 * @author danbin++
 * 屏幕分辨率适配方案
 *
 * <br> 目前设计图按照750 * 1334分辨率，但是为了好计算Android客户端适配按照720 * 1280去适配 </br>
 */
public class AndroidDpConvert {
    public static void main(String[] args) {
        AndroidDpConvertMobile mobile = new AndroidDpConvertMobile();
        mobile.convert();
    }
}