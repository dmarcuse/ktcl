package me.apemanzilla.ktcl.cl10

import me.apemanzilla.ktcl.CLContext
import me.apemanzilla.ktcl.CLDevice
import me.apemanzilla.ktcl.CLException
import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10
import org.lwjgl.opencl.CL10.CL_CONTEXT_DEVICES
import org.lwjgl.system.MemoryUtil

val CLContext.devices get() = info.pointers(CL_CONTEXT_DEVICES).map { CLDevice(it) }

/** Create an OpenCL context with this device */
fun CLDevice.createContext() = CLException.checkErr { e ->
	val props = BufferUtils.createPointerBuffer(3)
			.put(CL10.CL_CONTEXT_PLATFORM.toLong()).put(platform.handle)
			.put(MemoryUtil.NULL).flip()

	CLContext(CL10.clCreateContext(props, handle, null, MemoryUtil.NULL, e), false)
}

/**
 * Create an OpenCL context with these devices
 * @throws IllegalArgumentException when no devices are specified or devices are not all from the same platform
 */
fun Iterable<CLDevice>.createContext() = CLException.checkErr { e ->
	require(any()) { "Context requires at least one device" }
	val platforms = map { it.platform }.distinct()
	require(platforms.count() == 1) { "Devices must be from same platform" }
	val platform = platforms.first()

	val devices = BufferUtils.createPointerBuffer(count())
	forEach { devices.put { it.handle } }
	devices.flip()

	val props = BufferUtils.createPointerBuffer(3)
			.put(CL10.CL_CONTEXT_PLATFORM.toLong()).put(platform.handle)
			.put(MemoryUtil.NULL).flip()

	CLContext(CL10.clCreateContext(props, devices, null, MemoryUtil.NULL, e), false)
}
