package me.apemanzilla.ktcl

import me.apemanzilla.ktcl.cl10.name
import org.lwjgl.opencl.CL10.clGetDeviceInfo

// todo: properly support subdevices
class CLDevice internal constructor(handle: Long) : CLObject(handle, ::clGetDeviceInfo) {
	override val descriptor get() = name
}
