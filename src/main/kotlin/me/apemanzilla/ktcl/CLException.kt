package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.CL_SUCCESS
import java.nio.IntBuffer

/**
 * An exception raised by the OpenCL implementation
 * @param code The OpenCL error code
 */
class CLException internal constructor(val code: Int, message: String? = null)
	: RuntimeException("OpenCL error $code ${message?.let { ": $message" } ?: ""}") {
	internal companion object {
		inline fun checkErr(err: Int, lazyMessage: (Int) -> String?) {
			if (err != CL_SUCCESS) throw CLException(err, lazyMessage(err))
		}

		fun checkErr(err: Int) = checkErr(err) { null }

		inline fun <T> checkErr(block: (IntBuffer) -> T): T {
			val errBuf = BufferUtils.createIntBuffer(1).put(CL_SUCCESS)
			val result = block(errBuf)
			checkErr(errBuf[0])
			return result
		}
	}
}
