package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL.createDeviceCapabilities
import org.lwjgl.opencl.CL.createPlatformCapabilities
import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL20.CL_QUEUE_SIZE
import org.lwjgl.opencl.CL20.clCreateCommandQueueWithProperties
import org.lwjgl.opencl.CL21.CL_QUEUE_DEVICE_DEFAULT
import org.lwjgl.system.MemoryUtil.NULL

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
}