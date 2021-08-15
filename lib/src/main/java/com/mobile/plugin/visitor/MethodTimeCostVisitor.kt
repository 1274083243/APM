package com.mobile.plugin.visitor

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter


/**
 * @author ike.Liu
 * @date 2021年08月14日 22:40
 */
class MethodTimeCostVisitor(
    api: Int,
    methodVisitor: MethodVisitor,
    access: Int,
    name: String?,
    descriptor: String?
) : AdviceAdapter(api, methodVisitor, access, name, descriptor) {
    override fun onMethodEnter() {
        super.onMethodEnter()
        mv.visitLdcInsn(name)
        mv.visitMethodInsn(
            INVOKESTATIC,
            "com/apm/apm_interface/MethodTracer",
            "recordMethodStart",
            "(Ljava/lang/String;)V",
            false
        );
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        mv.visitLdcInsn(name)
        mv.visitMethodInsn(
            INVOKESTATIC,
            "com/apm/apm_interface/MethodTracer",
            "recordMethodEnd",
            "(Ljava/lang/String;)V",
            false
        );
//        if (name == "setImageBitmap") {
//            mv.visitMethodInsn(INVOKESTATIC, "com/apm/apm_interface/LargeImageViewTracker", "checkBitmap", "(Landroid/graphics/Bitmap;Landroid/view/View;)V", false);
//        }
//        if (name == "setImageDrawable") {
//            println("当前Image的Hook方法为:$name")
//            mv.visitMethodInsn(
//                INVOKESTATIC,
//                "com/apm/apm_interface/LargeImageViewTracker",
//                "checkDrawable",
//                "(Landroid/graphics/drawable/Drawable;Landroid/view/View;)V",
//                false
//            );
//
//        }
    }
}