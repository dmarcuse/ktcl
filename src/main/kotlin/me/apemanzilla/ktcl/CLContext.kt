package me.apemanzilla.ktcl

import me.apemanzilla.ktcl.CLException.Companion.checkErr
import org.lwjgl.opencl.CL10.*

class CLContext internal constructor(handle: Long, retain: Boolean) : CLObject(handle, ::clGetContextInfo) {
	init {
		if (retain) checkErr(clRetainContext(handle))
	}

	override val releaseFn = ::clReleaseContext
}
