package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10
import org.lwjgl.opencl.CL12.*

class CLKernel internal constructor(handle: Long, retain: Boolean) : CLObject(handle, ::clGetKernelInfo) {
	init {
		if (retain) checkErr(clRetainKernel(handle))
	}

	public override val name by lazy { info.ascii(CL_KERNEL_FUNCTION_NAME)!! }
	val numArgs by lazy { info.uint(CL_KERNEL_NUM_ARGS) }
	val context by lazy { CLContext(info.pointer(CL_KERNEL_CONTEXT), true) }
	val program by lazy { CLProgram(info.pointer(CL_KERNEL_PROGRAM), true) }
	val attributes by lazy { info.ascii(CL_KERNEL_ATTRIBUTES)!! }

	fun setArg(i: Int, v: Byte) = checkErr(clSetKernelArg1b(handle, i, v))
	fun setArg(i: Int, v: Short) = checkErr(clSetKernelArg1s(handle, i, v))
	fun setArg(i: Int, v: Int) = checkErr(clSetKernelArg1i(handle, i, v))
	fun setArg(i: Int, v: Long) = checkErr(clSetKernelArg1l(handle, i, v))
	fun setArg(i: Int, v: Float) = checkErr(clSetKernelArg1f(handle, i, v))
	fun setArg(i: Int, v: Double) = checkErr(clSetKernelArg1d(handle, i, v))
	fun setArg(i: Int, v: CLMem) = checkErr(clSetKernelArg1p(handle, i, v.handle))
}

fun CLProgram.createKernel(name: String) = checkErr { e -> CLKernel(clCreateKernel(handle, name, e), false) }

fun CLCommandQueue.enqueueTask(kernel: CLKernel) = checkErr(clEnqueueTask(handle, kernel.handle, null, null))

fun CLCommandQueue.enqueueNDRangeKernel(kernel: CLKernel, globalWorkSize: Long) {
	val sizeBuf = BufferUtils.createPointerBuffer(1).put(globalWorkSize).flip()
	checkErr(CL10.clEnqueueNDRangeKernel(handle, kernel.handle, 1, null, sizeBuf, null, null, null))
}
