package com.mobile.plugin

import com.android.build.gradle.AppExtension
import com.mobile.plugin.ext.getAndroidExtension
import com.mobile.plugin.model.ApmConfigExtension
import com.mobile.plugin.transform.MethodCostTimeTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @title APM 插件
 * @description
 * @param
 * @updateTime 2021/8/14 0014 14:45
 * @throws
 */
class ApmPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("开始初始化ApmPlugin")
        // 创建拓展属性
        project.extensions.create("apmConfig", ApmConfigExtension::class.java)
        project.afterEvaluate {
            println("gradle 配置阶段结束")
        }
        // 注册transform组件
        project.getAndroidExtension<AppExtension>().apply {
            println("transform组件注册完毕")
            registerTransform(MethodCostTimeTransform())
        }

    }

}