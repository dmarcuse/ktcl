package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL11.*
import org.lwjgl.opencl.CL12.*
import java.nio.IntBuffer

class CLPlatform internal constructor(id: Long) : CLObject(id) {
	private val info = CLInfoWrapper(id, ::clGetPlatformInfo)

	fun getDevices(): List<CLDevice> {
		// TODO
		val type = CL_DEVICE_TYPE_ALL.toLong()
		
		val sizeBuf = BufferUtils.createIntBuffer(1)		
		checkCLError(clGetDeviceIDs(id, type, null, sizeBuf))
		
		val devices = BufferUtils.createPointerBuffer(sizeBuf[0])
		checkCLError(clGetDeviceIDs(id, type, devices, null as IntBuffer?))
		return List(sizeBuf[0]) { i -> CLDevice(devices[i]) }
	}
	
	fun unloadCompiler() = checkCLError(clUnloadPlatformCompiler(id))
	
	val profile by info.string(CL_PLATFORM_PROFILE)
	val version by info.string(CL_PLATFORM_VERSION)
	val name by info.string(CL_PLATFORM_NAME)
	val vendor by info.string(CL_PLATFORM_VENDOR)
	val extensions by info.string(CL_PLATFORM_EXTENSIONS).then { it.split(" ") }

	override fun toString() = "${super.toString()}: $name"
}

fun getPlatforms(): List<CLPlatform> {
	val sizeBuf = BufferUtils.createIntBuffer(1)
	checkCLError(clGetPlatformIDs(null, sizeBuf))

	val platforms = BufferUtils.createPointerBuffer(sizeBuf[0])
	checkCLError(clGetPlatformIDs(platforms, null as IntBuffer?))
	return List(sizeBuf[0]) { i -> CLPlatform(platforms[i]) }
}

fun getDefaultPlatform() = getPlatforms().first()
