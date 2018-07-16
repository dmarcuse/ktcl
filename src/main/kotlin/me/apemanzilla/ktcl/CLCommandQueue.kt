package me.apemanzilla.ktcl

import org.lwjgl.opencl.CL10.*
import org.lwjgl.system.MemoryUtil.NULL

class CLCommandQueue internal constructor(handle: Long, retain: Boolean = true) : CLObject(handle, ::clGetCommandQueueInfo) {
	init {
		if (retain) checkErr(clRetainCommandQueue(handle))
	}

	override val releaseFn = ::clReleaseCommandQueue

	val context by lazy { CLContext(info.pointer(CL_QUEUE_CONTEXT), true) }
	val device by lazy { CLDevice(info.pointer(CL_QUEUE_DEVICE)) }

	fun flush() = checkErr(clFlush(handle))
	fun finish() = checkErr(clFinish(handle))
}

// TODO: Investigate using clCreateCommandQueueWithProperties on OpenCL 2.0+ platforms/devices?
fun CLDevice.createCommandQueue(ctx: CLContext) = checkErr { e ->
	CLCommandQueue(clCreateCommandQueue(ctx.handle, handle, NULL, e), false)
}
