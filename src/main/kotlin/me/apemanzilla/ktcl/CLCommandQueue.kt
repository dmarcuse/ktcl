package me.apemanzilla.ktcl

import org.lwjgl.opencl.CL10.*

class CLCommandQueue internal constructor(id: Long) : CLObject(id) {
	private val info = CLInfoWrapper(id, ::clGetCommandQueueInfo)
	
	val context by info.ptr(CL_QUEUE_CONTEXT).then(::CLContext)
	val device by info.ptr(CL_QUEUE_DEVICE).then(::CLDevice)
	// CL_QUEUE_PROPERTIES
}