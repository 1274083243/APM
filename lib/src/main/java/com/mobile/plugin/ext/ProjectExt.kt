package com.mobile.plugin.ext

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project

/**
 * @author ike.Liu
 * @date 2021年08月14日 15:42\
 * @des project的拓展类
 */
/**
 * 获取Android Plugin的拓展信息
 */
inline fun <reified T : BaseExtension> Project.getAndroidExtension():T =
    extensions.getByName("android") as T
