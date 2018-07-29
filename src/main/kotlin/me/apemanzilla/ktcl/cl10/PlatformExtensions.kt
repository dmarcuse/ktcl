package me.apemanzilla.ktcl.cl10

import me.apemanzilla.ktcl.CLException.Companion.checkErr
import me.apemanzilla.ktcl.CLPlatform
import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.*
import java.nio.IntBuffer

val CLPlatform.profile get() = info.ascii(CL_PLATFORM_PROFILE)
val CLPlatform.version get() = info.ascii(CL_PLATFORM_VERSION)
val CLPlatform.name get() = info.ascii(CL_PLATFORM_NAME)
val CLPlatform.vendor get() = info.ascii(CL_PLATFORM_VENDOR)
val CLPlatform.extensions get() = info.ascii(CL_PLATFORM_EXTENSIONS).split(" ")

/** Get all available OpenCL platforms */
fun getPlatforms(): List<CLPlatform> {
	val sizeBuf = BufferUtils.createIntBuffer(1)
	checkErr(clGetPlatformIDs(null, sizeBuf))

	val platforms = BufferUtils.createPointerBuffer(sizeBuf[0])
	checkErr(clGetPlatformIDs(platforms, null as IntBuffer?))
	return List(sizeBuf[0]) { i -> CLPlatform(platforms[i]) }
}
