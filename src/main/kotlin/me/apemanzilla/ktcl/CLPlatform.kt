package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.*

/**
 * An OpenCL platform
 */
class CLPlatform internal constructor(handle: Long) : CLObject(handle, ::clGetPlatformInfo) {
	public override val name by lazy { info.ascii(CL_PLATFORM_NAME)!! }
	val profile by lazy { info.ascii(CL_PLATFORM_PROFILE)!! }
	val version by lazy { info.ascii(CL_PLATFORM_VERSION)!! }
	val vendor by lazy { info.ascii(CL_PLATFORM_VENDOR)!! }
	val extensions by lazy { info.ascii(CL_PLATFORM_EXTENSIONS)!!.split(" ") }

	fun hasExtension(ext: String) = ext in extensions
}

/**
 * Get a list of all OpenCL platforms
 */
fun getPlatforms(): List<CLPlatform> {
	val sizeBuf = BufferUtils.createIntBuffer(1)
	checkErr(clGetPlatformIDs(null, sizeBuf))

	val platforms = BufferUtils.createPointerBuffer(sizeBuf[0])
	checkErr(clGetPlatformIDs(platforms, sizeBuf))
	return List(sizeBuf[0]) { i -> CLPlatform(platforms[i]) }
}
