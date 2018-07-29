package me.apemanzilla.ktcl

import me.apemanzilla.ktcl.CLException.Companion.checkErr
import org.lwjgl.opencl.CL10.*

class CLCommandQueue internal constructor(handle: Long, retain: Boolean) : CLObject(handle, ::clGetCommandQueueInfo) {
	init {
		if (retain) checkErr(clRetainCommandQueue(handle))
	}

	override val releaseFn = ::clReleaseCommandQueue
}
