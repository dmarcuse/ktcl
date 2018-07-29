package me.apemanzilla.ktcl

import me.apemanzilla.ktcl.CLException.Companion.checkErr
import org.lwjgl.opencl.CL10.clGetProgramInfo
import org.lwjgl.opencl.CL10.clRetainProgram

class CLProgram internal constructor(handle: Long, retain: Boolean) : CLObject(handle, ::clGetProgramInfo) {
	init {
		if(retain) checkErr(clRetainProgram(handle))
	}

	class BuildInfo internal constructor(val info: CLInfoWrapper)
}
