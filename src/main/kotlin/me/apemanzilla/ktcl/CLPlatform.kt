package me.apemanzilla.ktcl

import me.apemanzilla.ktcl.cl10.name
import org.lwjgl.opencl.CL10.clGetPlatformInfo

class CLPlatform internal constructor(handle: Long) : CLObject(handle, ::clGetPlatformInfo) {
	override val descriptor get() = name
}
