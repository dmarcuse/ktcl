package me.apemanzilla.ktcl.cl10

import me.apemanzilla.ktcl.CLBuffer
import me.apemanzilla.ktcl.CLCommandQueue
import me.apemanzilla.ktcl.CLContext
import me.apemanzilla.ktcl.CLEvent
import me.apemanzilla.ktcl.CLException.Companion.checkErr
import me.apemanzilla.ktcl.cl10.KernelAccess.ReadWrite
import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.*
import java.nio.ByteBuffer

/**
 * Create an OpenCL buffer with the given host data pointer and flags
 * @throws IllegalArgumentException when the host data buffer is not direct
 */
fun CLContext.createBuffer(data: ByteBuffer, flags: MemFlag = ReadWrite) = checkErr { e ->
	require(data.isDirect) { "Data buffer must be direct" }
	data.rewind()

	CLBuffer(clCreateBuffer(handle, flags.mask.toLong(), data, e), false)
}

/**
 * Create an OpenCL buffer with the given size (in bytes). Note that there is no host data pointer, so using flags which
 * require one will result in an OpenCL error.
 */
fun CLContext.createBuffer(size: Long, flags: MemFlag = ReadWrite) = checkErr { e ->
	CLBuffer(clCreateBuffer(handle, flags.mask.toLong(), size, e), false)
}

/**
 * Read an OpenCL buffer into a [ByteBuffer].
 * @throws IllegalArgumentException when the [ByteBuffer] is not direct
 */
fun CLCommandQueue.enqueueReadBuffer(
		from: CLBuffer,
		to: ByteBuffer,
		offset: Long = 0,
		blocking: Boolean = true): CLEvent {
	require(to.isDirect) { "Data buffer must be direct" }
	to.rewind()

	val eventBuf = BufferUtils.createPointerBuffer(1)
	checkErr(clEnqueueReadBuffer(handle, from.handle, blocking, offset, to, null, eventBuf))
	return CLEvent(eventBuf[0], true)
}

/**
 * Write a [ByteBuffer] into an OpenCL buffer.
 * @throws IllegalArgumentException when the [ByteBuffer] is not direct
 */
fun CLCommandQueue.enqueueWriteBuffer(
		from: ByteBuffer,
		to: CLBuffer,
		offset: Long = 0,
		blocking: Boolean = true
): CLEvent {
	require(from.isDirect) { "Data buffer must be direct" }
	from.rewind()

	val eventBuf = BufferUtils.createPointerBuffer(1)
	checkErr(clEnqueueWriteBuffer(handle, to.handle, blocking, offset, from, null, eventBuf))
	return CLEvent(eventBuf[0], true)
}
