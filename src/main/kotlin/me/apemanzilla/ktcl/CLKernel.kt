package me.apemanzilla.ktcl

import me.apemanzilla.ktcl.CLException.Companion.checkErr
import org.lwjgl.opencl.CL10.*

class CLKernel internal constructor(handle: Long, retain: Boolean) : CLObject(handle, ::clGetKernelInfo) {
	init {
		if (retain) checkErr(clRetainKernel(handle))
	}

	override val releaseFn = ::clReleaseKernel
}
