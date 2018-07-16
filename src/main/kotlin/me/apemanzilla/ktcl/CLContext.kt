package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.*
import org.lwjgl.system.MemoryUtil.NULL

class CLContext internal constructor(handle: Long, retain: Boolean) : CLObject(handle, ::clGetContextInfo) {
	init {
		if (retain) checkErr(clRetainContext(handle))
	}

	override val releaseFn = ::clReleaseContext

	val devices by lazy { info.pointers(CL_CONTEXT_DEVICES).map { CLDevice(it) } }

	operator fun contains(dev: CLDevice) = dev in devices
}

/**
 * Create an OpenCL context for the given device
 */
fun CLDevice.createContext() = CLContext(checkErr { e -> clCreateContext(null, handle, null, NULL, e) }, false)

/**
 * Create an OpenCL context for the given devices
 */
fun Iterable<CLDevice>.createContext() = toList().run {
	require(size > 0) { "At least one device must be present" }
	require(map { it.platform }.distinct().count() == 1) { "Devices must be from same platform" }

	val devices = BufferUtils.createPointerBuffer(size)
	forEach { devices.put(it.handle) }
	devices.flip()

	return@run CLContext(checkErr { e -> clCreateContext(null, devices, null, NULL, e) }, false)
}

/**
 * Creates an OpenCL context using devices of the given [type][CLDevice.Type] on the given platform
 */
fun CLPlatform.createContext(deviceType: CLDevice.Type = CLDevice.Type.Default): CLContext {
	val props = BufferUtils.createPointerBuffer(3)
			.put(CL_CONTEXT_PLATFORM.toLong()).put(handle)
			.put(NULL).flip()

	return CLContext(checkErr { e -> clCreateContextFromType(props, deviceType.type.toLong(), null, NULL, e) }, false)
}

/**
 * Creates an OpenCL context using the given [type][CLDevice.Type]
 */
fun createContext(deviceType: CLDevice.Type = CLDevice.Type.Default) = CLContext(
		checkErr { e -> clCreateContextFromType(null, deviceType.type.toLong(), null, NULL, e) },
		retain = false
)
