package me.apemanzilla.ktcl

import org.lwjgl.opencl.CL10.clGetMemObjectInfo
import org.lwjgl.opencl.CL10.clRetainMemObject

abstract class CLMem internal constructor(handle: Long, retain: Boolean) : CLObject(handle, ::clGetMemObjectInfo) {
	init {
		if (retain) checkErr(clRetainMemObject(handle))
	}
}
