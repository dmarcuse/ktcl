package me.apemanzilla.ktcl

import org.lwjgl.opencl.CL10.*

open class CLException internal constructor(val errorCode: Int, msg: String? = null) : Exception("OpenCL Error $errorCode" + (msg?.let { ": $it" } ?: ""))

class CLOutOfHostMemoryException : CLException(CL_OUT_OF_HOST_MEMORY, "Out of host memory")
class CLInvalidValueException : CLException(CL_INVALID_VALUE, "Invalid value")
class CLDeviceNotAvailableException : CLException(CL_DEVICE_NOT_AVAILABLE, "Device not available")
class CLDeviceNotFoundException : CLException(CL_DEVICE_NOT_FOUND, "Device not found")
class CLProgramBuildException(log: String? = null) : CLException(CL_BUILD_PROGRAM_FAILURE, log ?: "No log available")

fun createCLError(code: Int): CLException = when (code) {
	CL_INVALID_VALUE -> CLInvalidValueException()
	CL_OUT_OF_HOST_MEMORY -> CLOutOfHostMemoryException()
	CL_DEVICE_NOT_AVAILABLE -> CLDeviceNotAvailableException()
	CL_DEVICE_NOT_FOUND -> CLDeviceNotFoundException()
	CL_BUILD_PROGRAM_FAILURE -> CLProgramBuildException()
	else -> CLException(code)
}

inline fun checkCLError(status: Int, onErr: (Int) -> Throwable?) {
	if (status != CL_SUCCESS) throw (onErr(status) ?: createCLError(status))
}

fun checkCLError(status: Int) = checkCLError(status, ::createCLError)
