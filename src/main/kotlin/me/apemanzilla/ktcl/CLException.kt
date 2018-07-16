package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.CL_SUCCESS
import java.nio.IntBuffer

private fun String?.prepend(s: String) = this?.let { s + it }

class CLException(val code: Int, msg: String? = null) : Exception("OpenCL Error $code ${msg.prepend(": ") ?: ""}}")

internal inline fun checkErr(code: Int, msg: (Int) -> String?) = if (code != CL_SUCCESS) throw CLException(code, msg(code)) else Unit
internal fun checkErr(code: Int) = if (code != CL_SUCCESS) throw CLException(code) else Unit

internal inline fun <T> checkErr(block: (IntBuffer) -> T): T {
	val errBuf = BufferUtils.createIntBuffer(1)
	val ret = block(errBuf)
	checkErr(errBuf[0])
	return ret
}
