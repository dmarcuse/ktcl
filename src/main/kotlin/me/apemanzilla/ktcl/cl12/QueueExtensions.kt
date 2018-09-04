package me.apemanzilla.ktcl.cl12

import me.apemanzilla.ktcl.CLBuffer
import me.apemanzilla.ktcl.CLCommandQueue
import me.apemanzilla.ktcl.CLException.Companion.checkErr
import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL12.clEnqueueFillBuffer
import java.nio.ByteBuffer

fun CLCommandQueue.enqueueFillBuffer(buffer: CLBuffer, pattern: ByteBuffer) {
	require(pattern.isDirect) { "Pattern buffer must be direct" }
	checkErr(clEnqueueFillBuffer(handle, buffer.handle, pattern, 0, pattern.remaining().toLong(), null, null))
}

fun CLCommandQueue.enqueueFillBuffer(buffer: CLBuffer, pattern: ByteArray) = enqueueFillBuffer(
		buffer,
		BufferUtils.createByteBuffer(pattern.size).apply { put(pattern).flip() }
)
