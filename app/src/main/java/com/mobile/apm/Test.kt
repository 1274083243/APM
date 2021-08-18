package com.mobile.apm

import android.app.Activity
import android.content.Intent

/**
 * @author ike.Liu
 * @date 2021年08月14日 22:46
 */
inline fun <reified T: Activity> Activity.starActivity1(){
    startActivity(Intent(this,T::class.java))
}