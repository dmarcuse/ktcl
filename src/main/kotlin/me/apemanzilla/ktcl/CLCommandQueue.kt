package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.PointerBuffer
import org.lwjgl.opencl.CL.createDeviceCapabilities
import org.lwjgl.opencl.CL.createPlatformCapabilities
import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL20.CL_QUEUE_SIZE
import org.lwjgl.opencl.CL20.clCreateCommandQueueWithProperties
import org.lwjgl.opencl.CL21.CL_QUEUE_DEVICE_DEFAULT
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.*

/**
 * Creates a new command queue, returning the handle
 */
private fun createQueue(ctx: CLContext, dev: CLDevice): Long {
	val platCaps = createPlatformCapabilities(dev.platform.handle)
	val devCaps = createDeviceCapabilities(dev.handle, platCaps)

	val errBuf = BufferUtils.createIntBuffer(1)

	// OpenCL 2.0 deprecated clCreateCommandQueue, so we use the replacement instead for 2.0+
	val handle = if (devCaps.OpenCL20)
		clCreateCommandQueueWithProperties(ctx.handle, dev.handle, null, errBuf)
	else
		clCreateCommandQueue(ctx.handle, dev.handle, NULL, errBuf)

	checkCLError(errBuf[0])

	return handle
}

private fun Array<out CLEvent>.pointers() = when (size) {
	0 -> null
	else -> BufferUtils.createPointerBuffer(size).also { buf -> forEach { e -> buf.put(e.handle) } }.flip()
}

private fun pointerBufferOf(vararg values: Long) = when (values.size) {
	0 -> null
	else -> BufferUtils.createPointerBuffer(values.size).also { buf -> values.forEach { buf.put(it) } }.flip()
}

/**
 * An OpenCL command queue
 */
class CLCommandQueue : CLObject {
	/**
	 * Properties from an existing command queue
	 */
	class Properties internal constructor(bits: Long) {
		val b = BitField(bits)

		val outOfOrderExecModeEnable get() = b.test(CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE)
		val profilingEnable get() = b.test(CL_QUEUE_PROFILING_ENABLE)
	}

	/**
	 * Creates a command queue from an existing handle, calling [clRetainCommandQueue].
	 * Should not be used for new command queues, only existing ones.
	 */
	internal constructor(handle: Long) : super(handle, ::clReleaseCommandQueue) {
		checkCLError(clRetainCommandQueue(handle))
	}

	/**
	 * Creates a new command queue for the given [context][CLContext] and [device][CLDevice].
	 */
	constructor(context: CLContext, device: CLDevice) : super(createQueue(context, device), ::clReleaseCommandQueue)

	private val info = CLInfo(handle, ::clGetCommandQueueInfo)

	val context by info.pointer(CL_QUEUE_CONTEXT).then(::CLContext)
	val device by info.pointer(CL_QUEUE_DEVICE).then(::CLDevice)
	val referenceCount by info.uint(CL_QUEUE_REFERENCE_COUNT)
	val properties by info.long(CL_QUEUE_PROPERTIES).then(CLCommandQueue::Properties)
	val size by info.uint(CL_QUEUE_SIZE)
	val deviceDefault by info.pointer(CL_QUEUE_DEVICE_DEFAULT).then(::CLDevice)

	/**
	 * Wraps the given call, providing an event buffer to pass to OpenCL calls and handling status codes automatically
	 */
	private inline fun wrap(f: (PointerBuffer) -> Int): CLEvent {
		val evtBuf = BufferUtils.createPointerBuffer(1)
		checkCLError(f(evtBuf))
		return CLEvent(evtBuf[0])
	}

	fun flush() = checkCLError(clFlush(handle))

	fun finish() = checkCLError(clFinish(handle))

	fun enqueueReadBuffer(from: CLBuffer, to: ByteBuffer, blocking: Boolean = false, offset: Long = 0, vararg events: CLEvent) =
			wrap { e -> clEnqueueReadBuffer(handle, from.handle, blocking, offset, to, events.pointers(), e) }

	fun enqueueWriteBuffer(to: CLBuffer, from: ByteBuffer, blocking: Boolean = false, offset: Long = 0, vararg events: CLEvent) =
			wrap { e -> clEnqueueWriteBuffer(handle, to.handle, blocking, offset, from, events.pointers(), e) }

	// todo: figure out why overloads for other buffers or arrays keep erroring

	fun enqueueNDRangeKernel(kernel: CLKernel, globalOffset: Long, globalSize: Long, vararg events: CLEvent) =
			wrap { e -> clEnqueueNDRangeKernel(handle, kernel.handle, 1, pointerBufferOf(globalOffset), pointerBufferOf(globalSize), null, events.pointers(), e) }
}