package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL11.CL_CONTEXT_NUM_DEVICES
import org.lwjgl.system.MemoryUtil.NULL

/**
 * An OpenCL context
 */
class CLContext : CLObject {
	/**
	 * Configurable options used when creating a [CLContext]
	 */
	data class Properties(
			val platform: CLPlatform,
			val interopUserSync: Boolean? = null,
			val terminable: Boolean? = null
	)

	/**
	 * Creates a context from an existing handle, calling [clRetainContext].
	 * Should not be used for new contexts, only existing ones.
	 */
	internal constructor(handle: Long) : super(handle, ::clReleaseContext) {
		checkCLError(clRetainContext(handle))
	}

	private companion object {
		fun createCtx(devs: Iterable<CLDevice>): Long {
			val propBuf = BufferUtils.createPointerBuffer(1).put(NULL).flip()
			val devBuf = BufferUtils.createPointerBuffer(devs.count()).put(devs.map { it.handle }.toLongArray()).flip()
			val errBuf = BufferUtils.createIntBuffer(1)

			val handle = clCreateContext(propBuf, devBuf, null, NULL, errBuf)
			checkCLError(errBuf[0])
			return handle
		}
	}

	/**
	 * Creates a new context containing the given device
	 *
	 * @param dev The [CLDevice] this context uses
	 */
	constructor(dev: CLDevice) : super(createCtx(listOf(dev)), ::clReleaseContext)

	/**
	 * Creates a new context containing the given devices
	 *
	 * @param devs The [CLDevice]s this context uses
	 */
	constructor(devs: Iterable<CLDevice>) : super(createCtx(devs), ::clReleaseContext) {
		require(devs.any()) { "At least one device required" }
	}

	private val info: CLInfo = CLInfo(handle, ::clGetContextInfo)

	val referenceCount by info.uint(CL_CONTEXT_REFERENCE_COUNT)
	val numDevices by info.uint(CL_CONTEXT_NUM_DEVICES)
	val devices by lazy { info.getPointers(CL_CONTEXT_DEVICES).map(::CLDevice) }
	// CL_CONTEXT_PROPERTIES
	// CL_CONTEXT_D3D10_PREFER_SHARED_RESOURCES
	// CL_CONTEXT_D3D11_PREFER_SHARED_RESOURCES
}
