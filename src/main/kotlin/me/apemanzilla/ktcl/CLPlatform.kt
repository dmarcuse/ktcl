package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL.createPlatformCapabilities
import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL21.CL_PLATFORM_HOST_TIMER_RESOLUTION
import org.lwjgl.opencl.KHRICD.CL_PLATFORM_ICD_SUFFIX_KHR

/**
 * An OpenCL platform
 */
class CLPlatform internal constructor(handle: Long) : CLObject(handle) {
	internal val caps by lazy { createPlatformCapabilities(handle) }
	private val info = CLInfo(handle, ::clGetPlatformInfo, ::caps)

	val profile by info.string(CL_PLATFORM_PROFILE).CL10()
	val version by info.string(CL_PLATFORM_VERSION).CL10()
	val name by info.string(CL_PLATFORM_NAME).CL10()
	val vendor by info.string(CL_PLATFORM_VENDOR).CL10()
	val extensions by info.string(CL_PLATFORM_EXTENSIONS).CL10()
	val hostTimerResolution by info.ulong(CL_PLATFORM_HOST_TIMER_RESOLUTION).CL21()
	val icdSuffix get() = if (extensions.contains("cl_khr_icd")) info.getString(CL_PLATFORM_ICD_SUFFIX_KHR) else null

	override fun toString() = "CLPlatform %s [0x%x]".format(name, handle)

	/**
	 * Gets devices of the given type from this platform.
	 * @param type The device type. Defaults to the `ALL` device type, which selects all non-custom devices.
	 */
	fun getDevices(type: CLDevice.Type = CLDevice.Type.ALL) = getDevices(this, type)
}

/**
 * Gets all available platforms
 */
fun getAllPlatforms(): List<CLPlatform> {
	val sizeBuf = BufferUtils.createIntBuffer(1)
	checkCLError(clGetPlatformIDs(null, sizeBuf))
	sizeBuf.rewind()

	val platsBuf = BufferUtils.createPointerBuffer(sizeBuf[0])
	checkCLError(clGetPlatformIDs(platsBuf, sizeBuf))
	return List(sizeBuf[0]) { i -> CLPlatform(platsBuf[i]) }
}
