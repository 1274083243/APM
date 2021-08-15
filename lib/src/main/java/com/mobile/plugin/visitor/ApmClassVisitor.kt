package com.mobile.plugin.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.ASM6

/**
 * @author ike.Liu
 * @date 2021年08月14日 16:47
 */
class ApmClassVisitor(cv: ClassVisitor, api: Int = ASM6) : ClassVisitor(api, cv) {
    private val notTraceMethods = listOf("<init>", "<clinit>")
    private val monitorView = "com/mobile/apm/CustomImageView"
    private val superView = "android/widget/ImageView"
    private val superViewAppCompat = "androidx/appcompat/widget/AppCompatImageView"

    private var className = ""
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        var innerSuperView = superName
        if (!name.equals(monitorView)) {
            if (innerSuperView.equals(superView)) {
                innerSuperView = monitorView
                println("替换superView")
            }
            if (innerSuperView.equals(superViewAppCompat)) {
                innerSuperView = monitorView
                println("替换superViewAppCompat")

            }
        }
        super.visit(version, access, name, signature, innerSuperView, interfaces)
        this.className = name?.replace("/", ".") ?: ""
        println("当前类名:${className},$name,$superName")
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val isUnImplMethod = access and Opcodes.ACC_ABSTRACT > 0    //未实现的方法

        if (isUnImplMethod || notTraceMethods.contains(name)) {
            return super.visitMethod(access, name, descriptor, signature, exceptions)
        }
        println("当前方法名:${name},${access}")
        val methodName = "$className&$name()"
        val visitMethod = cv.visitMethod(access, name, descriptor, signature, exceptions)
        return MethodTimeCostVisitor(api, visitMethod, access, name, descriptor)
    }
}