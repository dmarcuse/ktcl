package me.apemanzilla.ktcl.cl10

import me.apemanzilla.ktcl.CLDevice
import me.apemanzilla.ktcl.CLException
import me.apemanzilla.ktcl.CLPlatform
import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL12.CL_DEVICE_DOUBLE_FP_CONFIG
import org.lwjgl.opencl.KHRFP16.CL_DEVICE_HALF_FP_CONFIG

val CLDevice.addressBits get() = info.uint(CL_DEVICE_ADDRESS_BITS)
val CLDevice.available get() = info.bool(CL_DEVICE_AVAILABLE)
val CLDevice.compilerAvailable get() = info.bool(CL_DEVICE_COMPILER_AVAILABLE)
val CLDevice.doubleFPConfig get() = info.long(CL_DEVICE_DOUBLE_FP_CONFIG).let { FPConfig(it) }
val CLDevice.endianLittle get() = info.bool(CL_DEVICE_ENDIAN_LITTLE)
val CLDevice.errorCorrectionSupport get() = info.bool(CL_DEVICE_ERROR_CORRECTION_SUPPORT)
val CLDevice.execCapabilities get() = info.long(CL_DEVICE_EXECUTION_CAPABILITIES).let { ExecCapabilities(it) }
val CLDevice.extensions get() = info.ascii(CL_DEVICE_EXTENSIONS).split(" ")
val CLDevice.globalMemCacheSize get() = info.ulong(CL_DEVICE_GLOBAL_MEM_CACHE_SIZE)
val CLDevice.globalMemCacheType get() = info.int(CL_DEVICE_GLOBAL_MEM_CACHE_TYPE).let { MemCacheType.get(it) }
val CLDevice.globalMemCacheLineSize get() = info.uint(CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE)
val CLDevice.globalMemSize get() = info.ulong(CL_DEVICE_GLOBAL_MEM_SIZE)
val CLDevice.halfFPConfig get() = info.long(CL_DEVICE_HALF_FP_CONFIG).let { FPConfig(it) }
val CLDevice.imageSupport get() = info.bool(CL_DEVICE_IMAGE_SUPPORT)
val CLDevice.image2dMaxHeight get() = info.size_t(CL_DEVICE_IMAGE2D_MAX_HEIGHT)
val CLDevice.image2dMaxWidth get() = info.size_t(CL_DEVICE_IMAGE2D_MAX_WIDTH)
val CLDevice.image3dMaxHeight get() = info.size_t(CL_DEVICE_IMAGE3D_MAX_HEIGHT)
val CLDevice.image3dMaxWidth get() = info.size_t(CL_DEVICE_IMAGE3D_MAX_WIDTH)
val CLDevice.image3dMaxDepth get() = info.size_t(CL_DEVICE_IMAGE3D_MAX_DEPTH)
val CLDevice.localMemSize get() = info.ulong(CL_DEVICE_LOCAL_MEM_SIZE)
val CLDevice.localMemType get() = info.int(CL_DEVICE_LOCAL_MEM_TYPE).let { LocalMemType.get(it) }
val CLDevice.maxClockFrequency get() = info.uint(CL_DEVICE_MAX_CLOCK_FREQUENCY)
val CLDevice.maxComputeUnits get() = info.uint(CL_DEVICE_MAX_COMPUTE_UNITS)
val CLDevice.maxConstantArgs get() = info.uint(CL_DEVICE_MAX_CONSTANT_ARGS)
val CLDevice.maxConstantBufferSize get() = info.ulong(CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE)
val CLDevice.maxMemAllocSize get() = info.ulong(CL_DEVICE_MAX_MEM_ALLOC_SIZE)
val CLDevice.maxParameterSize get() = info.size_t(CL_DEVICE_MAX_PARAMETER_SIZE)
val CLDevice.maxReadImageArgs get() = info.uint(CL_DEVICE_MAX_READ_IMAGE_ARGS)
val CLDevice.maxSamplers get() = info.uint(CL_DEVICE_MAX_SAMPLERS)
val CLDevice.maxWorkGroupSize get() = info.size_t(CL_DEVICE_MAX_WORK_GROUP_SIZE)
val CLDevice.maxWorkItemDimensions get() = info.uint(CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS)
val CLDevice.maxWorkItemSizes get() = info.size_ts(CL_DEVICE_MAX_WORK_ITEM_SIZES)
val CLDevice.maxWriteImageArgs get() = info.uint(CL_DEVICE_MAX_WRITE_IMAGE_ARGS)
val CLDevice.memBaseAddrAlign get() = info.uint(CL_DEVICE_MEM_BASE_ADDR_ALIGN)
val CLDevice.name get() = info.ascii(CL_DEVICE_NAME)
val CLDevice.platform get() = info.pointer(CL_DEVICE_PLATFORM).let { CLPlatform(it) }
val CLDevice.preferredVectorWidthChar get() = info.uint(CL_DEVICE_PREFERRED_VECTOR_WIDTH_CHAR)
val CLDevice.preferredVectorWidthShort get() = info.uint(CL_DEVICE_PREFERRED_VECTOR_WIDTH_SHORT)
val CLDevice.preferredVectorWidthInt get() = info.uint(CL_DEVICE_PREFERRED_VECTOR_WIDTH_INT)
val CLDevice.preferredVectorWidthLong get() = info.uint(CL_DEVICE_PREFERRED_VECTOR_WIDTH_LONG)
val CLDevice.preferredVectorWidthFloat get() = info.uint(CL_DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT)
val CLDevice.preferredVectorWidthDouble get() = info.uint(CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE)
val CLDevice.profilingTimerResolution get() = info.size_t(CL_DEVICE_PROFILING_TIMER_RESOLUTION)
val CLDevice.queueProperties get() = info.long(CL_DEVICE_QUEUE_PROPERTIES).let { QueueProperties(it) }
val CLDevice.singleFPConfig get() = info.long(CL_DEVICE_SINGLE_FP_CONFIG).let { FPConfig(it) }
val CLDevice.type get() = info.long(CL_DEVICE_TYPE).let { DeviceType(it) }
val CLDevice.vendor get() = info.ascii(CL_DEVICE_VENDOR)
val CLDevice.vendorID get() = info.uint(CL_DEVICE_VENDOR_ID)
val CLDevice.version get() = info.ascii(CL_DEVICE_VERSION)
val CLDevice.driverVersion get() = info.ascii(CL_DRIVER_VERSION)

/** Get a list of OpenCL devices provided by this platform, with a given type (defaulting to all standard devices) */
fun CLPlatform.getDevices(type: DeviceType = DeviceType.All): List<CLDevice> {
	val numDevices = BufferUtils.createIntBuffer(1)
	CLException.checkErr(clGetDeviceIDs(handle, type.mask, null, numDevices))

	// if no devices available, return immediately
	if (numDevices[0] <= 0) return listOf()

	val devices = BufferUtils.createPointerBuffer(numDevices[0])
	CLException.checkErr(clGetDeviceIDs(handle, type.mask, devices, numDevices.apply { rewind() }))
	return List(numDevices.rewind().remaining()) { i -> CLDevice(devices[i]) }
}

/** Get the default device provided by this platform */
fun CLPlatform.getDefaultDevice() = getDevices(DeviceType.Default).firstOrNull()
