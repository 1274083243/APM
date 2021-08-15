package com.mobile.plugin.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.builder.utils.zipEntry
import com.android.utils.FileUtils
import com.mobile.plugin.visitor.ApmClassVisitor
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.gradle.internal.impldep.org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream


/**
 * @author ike.Liu
 * @date 2021年08月14日 14:46
 * @des:方法耗时统计的Transform
 */
class MethodCostTimeTransform : Transform() {
    private val PACKAGE_NAME = "com\\mobile\\apm"
    override fun getName(): String = "MethodCostTimeTransform"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean = true

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        println("MethodCostTimeTransform 访问开始")
        val startTime = System.currentTimeMillis()
        val input = transformInvocation?.inputs
        val outPutProvider = transformInvocation?.outputProvider ?: return
        // 删除之前的输出
        // 分别遍历：目录：本地编译好的class文件
        // 和Jar：三方jar的依赖
        outPutProvider.deleteAll()

        input?.forEach { input ->
            input.directoryInputs.forEach {
                handleDirectory(it, outPutProvider)
            }
            input.jarInputs.forEach {
                handleJar(it, outPutProvider)
            }
        }
        val endTime = System.currentTimeMillis()
        println("MethodCostTimeTransform 访问结束,本次操作耗时：${endTime - startTime}")


    }

    /**
     * 处理jar类型的输入
     */
    private fun handleJar(jarInput: JarInput, outPutProvider: TransformOutputProvider) {
        val inputPath = jarInput.file.absolutePath
        if (inputPath.endsWith(".jar")) {
            // 截取文件的md5重命名文件，防止文件同名被覆盖
            var jarName = jarInput.name
            val md5Name = DigestUtils.md5Hex(inputPath)
            if (jarName.endsWith(".jar")) {
                jarName = jarName.subSequence(0, jarName.length - 4).toString()
            }
            // 创建备份缓存文件
            val jarFile = JarFile(jarInput.file)
            val entries = jarFile.entries()
            val tempFile = File(jarInput.file.parent + "/class_temp.jar")
            if (tempFile.exists()) {
                tempFile.delete()
            }
            // 创建输出流
            val jarOutputStream = JarOutputStream(FileOutputStream(tempFile))
            // 遍历jar文件下面的文件
            while (entries.hasMoreElements()) {
                val jarEntry = entries.nextElement()
                val entryName = jarEntry.name
                val zipEntry = zipEntry(entryName)
                // 获取jar文件的输入流
                val inputStream = jarFile.getInputStream(jarEntry)
                if (checkFileIsNeedProcess(entryName)) {
                    println("当前处理的文件是:$entryName")
                    jarOutputStream.putNextEntry(zipEntry)
                    // 创建ASM 类访问对象
                    val classReader = ClassReader(IOUtils.toByteArray(inputStream))
                    // COMPUTE_MAXS 栈帧的计算交由ASM自动计算
                    val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    val apmClassVisitor = ApmClassVisitor(classWriter)
                    classReader.accept(apmClassVisitor, EXPAND_FRAMES)
                    val codeArray = classWriter.toByteArray()
                    // 重新写入jar文件中
                    jarOutputStream.write(codeArray)
                } else {
                    // 不需要处理的文件
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            jarFile.close()
            // 生成输出路径
            val dest = outPutProvider.getContentLocation(
                jarName + md5Name, jarInput.contentTypes,
                jarInput.scopes, Format.JAR
            )
            println("生成的jar 目标路径:${dest.absolutePath}")
            FileUtils.copyFile(tempFile, dest)
            tempFile.delete()
        }
    }

    /**
     * 处理文件夹目录
     */
    private fun handleDirectory(input: DirectoryInput, outPutProvider: TransformOutputProvider) {
        val file = input.file
        if (file == null) {
            return
        }
        forEachDirectory(file)
        // 获取输出目录
        val dest = outPutProvider.getContentLocation(
            input.name,
            input.contentTypes, input.scopes, Format.DIRECTORY
        )
        println("文件输出位置:${dest.absolutePath}")
        FileUtils.copyFile(input.file, dest)
    }

    private fun forEachDirectory(file: File) {
//        if (file.absolutePath=="C:\\Users\\Administrator\\Desktop\\APM\\app\\build\\intermediates\\javac\\debug\\classes\\com\\mobile\\apm"){
//            println("是否是文件夹:${file.isDirectory}")
//            val listFiles = file.listFiles()
//            listFiles.forEach {
//                println("文件名:${it.isDirectory},${it.absolutePath}")
//
//            }
//
//        }
        if (file.isDirectory) {
            // 遍历文件
            val listFiles = file.listFiles()
            if (listFiles != null && listFiles.isNotEmpty()) {
                listFiles.forEach {

                    if (it.isDirectory) {
                        forEachDirectory(it)
                    } else {
                       processFile(it)
                    }
                }
            }

        }else{
            processFile(file)
        }
    }
    private fun processFile(file:File){
        val fileName = file.name
//        println("file.absolutePath:${file.absolutePath},${file.absolutePath.contains(PACKAGE_NAME)}")

        if (checkFileIsNeedProcess(file.absolutePath)) {
            println("当前处理的文件是:$fileName")
//                        println("-----deal with Directory class file <${fileName}'> -----------")
            val classReader = ClassReader(file.readBytes())
            val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
            val classVisitor = ApmClassVisitor(classWriter)
            classReader.accept(classVisitor, EXPAND_FRAMES)
            val codeByteArray = classWriter.toByteArray()
            val fileOutputStream = FileOutputStream("${file.parentFile}/${fileName}")
            fileOutputStream.write(codeByteArray)
            fileOutputStream.close()
        }
    }

    /**
     * 检查文件是否需要被处理
     */
    private fun checkFileIsNeedProcess(fileName: String): Boolean {
        println("fileName:${fileName}")
        if (fileName.contains(PACKAGE_NAME) && fileName.endsWith(".class") && !fileName.startsWith("R\$") && "R.class" != fileName
            && "BuildConfig.class" != fileName && !fileName.contains("R\$")||
            fileName.contains("AppCompatImageView")
        ) {
            return true
        }
        return false


    }

}