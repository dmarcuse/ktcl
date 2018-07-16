package me.apemanzilla.ktcl

import org.lwjgl.opencl.CL12.*
import org.lwjgl.system.MemoryUtil.NULL

class CLProgram internal constructor(handle: Long, retain: Boolean) : CLObject(handle, ::clGetProgramInfo) {
	init {
		if (retain) checkErr(clRetainProgram(handle))
	}

	override val releaseFn = ::clReleaseProgram

	val context by lazy { CLContext(info.pointer(CL_PROGRAM_CONTEXT), true) }
	val devices by lazy { info.pointers(CL_PROGRAM_DEVICES).map { CLDevice(it) } }
	val numKernels get() = info.size_t(CL_PROGRAM_NUM_KERNELS)
	val kernelNames get() = info.ascii(CL_PROGRAM_KERNEL_NAMES)!!.split(";")

	/** Get program build info for the given device */
	private fun buildInfo(dev: CLDevice) = object {
		val buildInfo = CLInfoWrapper { i, b, p -> clGetProgramBuildInfo(handle, dev.handle, i, b, p) }

		val status get() = info.int(CL_PROGRAM_BUILD_STATUS)
		val options get() = info.ascii(CL_PROGRAM_BUILD_OPTIONS)!!
		val log get() = info.ascii(CL_PROGRAM_BUILD_LOG)!!
	}

	/** Get the failure log from the first device that failed */
	private fun failureLog(): String? {
		devices.forEach { dev ->
			val info = buildInfo(dev)
			if (info.status == CL_BUILD_ERROR) {
				return "Device: $dev Log: ${info.log}"
			}
		}

		return null
	}

	/** Generate an appropriate error message for the given OpenCL error */
	private fun errorMessage(code: Int) = when (code) {
		CL_BUILD_PROGRAM_FAILURE -> "Build error: ${failureLog()}"
		CL_LINK_PROGRAM_FAILURE -> "Link error: ${failureLog()}"
		else -> null
	}

	/** Build the OpenCL program */
	fun build(options: String = "") {
		checkErr(clBuildProgram(handle, null, options, null, NULL), ::errorMessage)
	}

	// TODO: Asynchronous compilation
}

/** Create a new OpenCL program for this context with the given sources */
fun CLContext.createProgram(sources: Iterable<String>): CLProgram {
	val validSources = sources.filterNot(String::isEmpty).toTypedArray()
	require(validSources.isNotEmpty()) { "At least one non-empty source must be provided" }
	return checkErr { e -> CLProgram(clCreateProgramWithSource(handle, validSources, e), false) }
}

fun CLContext.createProgram(vararg sources: String) = createProgram(sources.asIterable())
