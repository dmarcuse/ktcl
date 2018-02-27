package me.apemanzilla.ktcl

import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL12.*

open class CLKernel internal constructor(id: Long) : CLObject(id) {
	private val info = CLInfoWrapper(id, ::clGetKernelInfo)

	val functionName by info.string(CL_KERNEL_FUNCTION_NAME)
	val numArgs by info.uint(CL_KERNEL_NUM_ARGS)
	val context by info.ptr(CL_KERNEL_CONTEXT).then(::CLContext)
	val program by info.ptr(CL_KERNEL_PROGRAM).then(::CLProgram)
	val attributes by info.string(CL_KERNEL_ATTRIBUTES)

	override fun toString() = "${super.toString()}: $functionName"
}