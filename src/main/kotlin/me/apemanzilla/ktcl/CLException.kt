package me.apemanzilla.ktcl

import org.lwjgl.opencl.CL10.*

open class CLException internal constructor(val errorCode: Int, msg: String? = null) : Exception("OpenCL Error $errorCode" + (msg?.let { ": " + it } ?: ""))

class CLOutOfHostMemoryException : CLException(CL_OUT_OF_HOST_MEMORY)
class CLInvalidValueException : CLException(CL_INVALID_VALUE)

fun createCLError(code: Int): CLException = when (code) {
	CL_INVALID_VALUE -> CLInvalidValueException()
	CL_OUT_OF_HOST_MEMORY -> CLOutOfHostMemoryException()
	else -> CLException(code)
}

inline fun checkCLError(status: Int, onErr: (Int) -> Throwable?) {
	if (status != CL_SUCCESS) throw (onErr(status) ?: createCLError(status))
}

fun checkCLError(status: Int) = checkCLError(status, ::createCLError)
