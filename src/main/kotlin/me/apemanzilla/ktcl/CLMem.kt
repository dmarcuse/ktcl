package me.apemanzilla.ktcl

import me.apemanzilla.ktcl.CLException.Companion.checkErr
import org.lwjgl.opencl.CL10.*

abstract class CLMem internal constructor(handle: Long, retain: Boolean) : CLObject(handle, ::clGetMemObjectInfo) {
	init {
		if (retain) checkErr(clRetainMemObject(handle))
	}

	override val releaseFn = ::clReleaseMemObject
}
