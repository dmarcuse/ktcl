package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.opencl.AMDDeviceAttributeQuery.CL_DEVICE_BOARD_NAME_AMD
import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL12.CL_DEVICE_TYPE_CUSTOM

class CLDevice internal constructor(handle: Long) : CLObject(handle, ::clGetDeviceInfo) {
	enum class Type(internal val type: Int) {
		CPU(CL_DEVICE_TYPE_CPU),
		GPU(CL_DEVICE_TYPE_GPU),
		Accelerator(CL_DEVICE_TYPE_ACCELERATOR),
		Default(CL_DEVICE_TYPE_DEFAULT),
		Custom(CL_DEVICE_TYPE_CUSTOM),
		All(CL_DEVICE_TYPE_ALL);

		internal companion object {
			fun get(type: Int) = values().first { it.type == type }
		}
	}

	val addressBits by lazy { info.uint(CL_DEVICE_ADDRESS_BITS) }
	val available get() = info.bool(CL_DEVICE_AVAILABLE)
	val compilerAvailable get() = info.bool(CL_DEVICE_COMPILER_AVAILABLE)
	val isLittleEndian by lazy { info.bool(CL_DEVICE_ENDIAN_LITTLE) }
	val extensions by lazy { info.ascii(CL_DEVICE_EXTENSIONS)!!.split(" ") }
	val imagesSupported by lazy { info.bool(CL_DEVICE_IMAGE_SUPPORT) }
	val maxClockFrequency by lazy { info.uint(CL_DEVICE_MAX_CLOCK_FREQUENCY) }
	val maxComputeUnits by lazy { info.uint(CL_DEVICE_MAX_COMPUTE_UNITS) }
	val rawName by lazy { info.ascii(CL_DEVICE_NAME)!! }
	val platform by lazy { CLPlatform(info.pointer(CL_DEVICE_PLATFORM)) }
	val profile by lazy { info.ascii(CL_DEVICE_PROFILE)!! }
	val type by lazy { Type.get(info.ulong(CL_DEVICE_TYPE).toInt()) }
	val vendor by lazy { info.ascii(CL_DEVICE_VENDOR)!! }
	val vendorID by lazy { info.uint(CL_DEVICE_VENDOR_ID) }
	val version by lazy { info.ascii(CL_DEVICE_VERSION)!! }
	val driverVersion by lazy { info.ascii(CL_DRIVER_VERSION)!! }

	val amd_boardName by lazy { info.ascii(CL_DEVICE_BOARD_NAME_AMD) ?: "unknown" }

	fun hasExtension(ext: String) = ext in extensions

	public override val name by lazy {
		when {
			hasExtension("cl_amd_device_attribute_query") -> "$rawName/$amd_boardName"
			else -> rawName
		}
	}
}

/**
 * Gets all devices of the given [type][CLDevice.Type] (defaulting to all non-custom devices) provided by this platform
 */
fun CLPlatform.getDevices(type: CLDevice.Type = CLDevice.Type.All): List<CLDevice> {
	val sizeBuf = BufferUtils.createIntBuffer(1)
	checkErr(clGetDeviceIDs(this.handle, type.type.toLong(), null, sizeBuf))

	val devices = BufferUtils.createPointerBuffer(sizeBuf[0])
	checkErr(clGetDeviceIDs(this.handle, type.type.toLong(), devices, sizeBuf))
	return List(sizeBuf[0]) { CLDevice(devices[it]) }
}

/**
 * Gets the [default][CLDevice.Type.Default] device for this platform, or null if no default device is available
 */
fun CLPlatform.getDefaultDevice() = getDevices(CLDevice.Type.Default).firstOrNull()

/**
 * Gets the default OpenCL device from the first platform providing one
 */
fun getDefaultDevice() = getPlatforms().map { it.getDefaultDevice() }.firstOrNull()
