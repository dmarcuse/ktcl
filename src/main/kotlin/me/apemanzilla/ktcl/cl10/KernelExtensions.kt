package me.apemanzilla.ktcl.cl10

import me.apemanzilla.ktcl.*
import me.apemanzilla.ktcl.CLException.Companion.checkErr
import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.*

val CLKernel.functionName get() = info.ascii(CL_KERNEL_FUNCTION_NAME)
val CLKernel.numArgs get() = info.uint(CL_KERNEL_NUM_ARGS)
val CLKernel.context get() = info.pointer(CL_KERNEL_CONTEXT).let { CLContext(it, true) }
val CLKernel.program get() = info.pointer(CL_KERNEL_PROGRAM).let { CLProgram(it, true) }

fun CLProgram.createKernel(name: String) = checkErr { e -> CLKernel(clCreateKernel(handle, name, e), false) }

fun CLKernel.setArg(idx: Int, value: Byte) = checkErr(clSetKernelArg1b(handle, idx, value))
fun CLKernel.setArg(idx: Int, value: Short) = checkErr(clSetKernelArg1s(handle, idx, value))
fun CLKernel.setArg(idx: Int, value: Int) = checkErr(clSetKernelArg1i(handle, idx, value))
fun CLKernel.setArg(idx: Int, value: Long) = checkErr(clSetKernelArg1l(handle, idx, value))
fun CLKernel.setArg(idx: Int, value: Float) = checkErr(clSetKernelArg1f(handle, idx, value))
fun CLKernel.setArg(idx: Int, value: Double) = checkErr(clSetKernelArg1d(handle, idx, value))

fun CLKernel.setArg(idx: Int, obj: CLMem) = checkErr(clSetKernelArg1p(handle, idx, obj.handle))

fun CLCommandQueue.enqueueTask(task: CLKernel): CLEvent {
	val eventBuf = BufferUtils.createPointerBuffer(1)
	checkErr(clEnqueueTask(handle, task.handle, null, eventBuf))
	return CLEvent(eventBuf[0], false)
}

fun CLCommandQueue.enqueueNDRangeKernel(kernel: CLKernel, globalSize: Long, globalOffset: Long = 0): CLEvent {
	val sizeBuf = BufferUtils.createPointerBuffer(1).put(globalSize).flip()
	val offsetBuf = BufferUtils.createPointerBuffer(1).put(globalOffset).flip()

	val eventBuf = BufferUtils.createPointerBuffer(1)
	checkErr(clEnqueueNDRangeKernel(handle, kernel.handle, 1, offsetBuf, sizeBuf, null, null, eventBuf))
	return CLEvent(eventBuf[0], false)
}

// TODO: offer a BakedNDRangeKernel class which reuses buffers for multiple invocations to improve performance here?
fun CLCommandQueue.enqueueNDRangeKernel(
		kernel: CLKernel,
		workDim: Int = 1,
		globalWorkOffset: LongArray? = null,
		globalWorkSize: LongArray,
		localWorkSize: LongArray? = null
): CLEvent {
	val globalOffsetBuf = globalWorkOffset?.let { BufferUtils.createPointerBuffer(it.size).put(it).flip() }
	val globalSizeBuf = globalWorkSize.let { BufferUtils.createPointerBuffer(it.size).put(it).flip() }
	val localSizeBuf = localWorkSize?.let { BufferUtils.createPointerBuffer(it.size).put(it).flip() }

	val eventBuf = BufferUtils.createPointerBuffer(1)
	checkErr(clEnqueueNDRangeKernel(handle, kernel.handle, workDim, globalOffsetBuf, globalSizeBuf, localSizeBuf, null, eventBuf))
	return CLEvent(eventBuf[0], false)
}
