package me.apemanzilla.ktcl

import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL11.*
import org.lwjgl.opencl.CL12.*
import org.lwjgl.system.MemoryUtil.NULL

enum class CLDeviceType(val mask: Long) {
	CPU(CL_DEVICE_TYPE_CPU),
	GPU(CL_DEVICE_TYPE_GPU),
	ACCELERATOR(CL_DEVICE_TYPE_ACCELERATOR),
	CUSTOM(CL_DEVICE_TYPE_CUSTOM),
	DEFAULT(CL_DEVICE_TYPE_DEFAULT),
	ALL(CL_DEVICE_TYPE_ALL);

	companion object {
		internal fun get(mask: Long) = CLDeviceType.values().first { it.checkMask(mask) }
	}

	private constructor(mask: Int) : this(mask.toLong())

	internal fun checkMask(otherMask: Long) = (otherMask and mask) == otherMask
}

class CLDeviceFPConfig internal constructor(private val mask: Long) {
	private fun checkMask(otherMask: Long) = (mask and otherMask) == otherMask
	private fun checkMask(otherMask: Int) = checkMask(otherMask.toLong())

	val denorms = checkMask(CL_FP_DENORM)
	val inf = checkMask(CL_FP_INF_NAN)
	val nan = checkMask(CL_FP_INF_NAN)
	val roundToNearest = checkMask(CL_FP_ROUND_TO_NEAREST)
	val roundToZero = checkMask(CL_FP_ROUND_TO_ZERO)
	val roundToInf = checkMask(CL_FP_ROUND_TO_INF)
	val fma = checkMask(CL_FP_FMA)
	val correctlyRoundedDivideSqrt = checkMask(CL_FP_CORRECTLY_ROUNDED_DIVIDE_SQRT)
	val softFloat = checkMask(CL_FP_SOFT_FLOAT)
	
	override fun toString(): String {
		val map = mapOf(
				"denorms" to denorms,
				"inf" to inf,
				"nan" to nan,
				"roundToNearest" to roundToNearest,
				"roundToZero" to roundToZero,
				"roundToInf" to roundToInf,
				"fma" to fma,
				"correctlyRoundedDivideSqrt" to correctlyRoundedDivideSqrt,
				"softFloat" to softFloat)
		
		return map.filter { it.value }.map { it.key }.joinToString(" ", "Supported: ")
	}
}

open class CLDevice internal constructor(id: Long) : CLObject(id) {
	fun createContext() = CLContext(this)

	private val info = CLInfoWrapper(id, ::clGetDeviceInfo)

	val addressBits by info.uint(CL_DEVICE_ADDRESS_BITS)
	val available by info.bool(CL_DEVICE_AVAILABLE)
	val builtInKernels by info.string(CL_DEVICE_BUILT_IN_KERNELS).then { it.split(";") }
	val compilerAvailable by info.bool(CL_DEVICE_COMPILER_AVAILABLE)
	val doubleFPConfig by info.ulong(CL_DEVICE_DOUBLE_FP_CONFIG).then(::CLDeviceFPConfig)
	val littleEndian by info.bool(CL_DEVICE_ENDIAN_LITTLE)
	val errorCorrectionSupported by info.bool(CL_DEVICE_ERROR_CORRECTION_SUPPORT)
	val nativeKernelsSupported by info.int(CL_DEVICE_EXECUTION_CAPABILITIES).then { it and CL_EXEC_NATIVE_KERNEL != 0 }
	val extensions by info.string(CL_DEVICE_EXTENSIONS).then { it.split(" ") }
	val globalMemCacheSize by info.ulong(CL_DEVICE_GLOBAL_MEM_CACHE_SIZE)
	val globalMemCacheReadable by info.int(CL_DEVICE_GLOBAL_MEM_CACHE_TYPE).then { it and (CL_READ_ONLY_CACHE or CL_READ_WRITE_CACHE) != 0 }
	val globalMemCacheWritable by info.int(CL_DEVICE_GLOBAL_MEM_CACHE_TYPE).then { it and CL_READ_WRITE_CACHE != 0 }
	val globalMemCachelineSize by info.uint(CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE)
	val globalMemSize by info.long(CL_DEVICE_GLOBAL_MEM_SIZE)
	// CL_DEVICE_HALF_FP_CONFIG
	val hostUnifiedMemory by info.bool(CL_DEVICE_HOST_UNIFIED_MEMORY)
	val imageSupport by info.bool(CL_DEVICE_IMAGE_SUPPORT)
	val image2dMaxHeight by info.size_t(CL_DEVICE_IMAGE2D_MAX_HEIGHT)
	val image2dMaxWidth by info.size_t(CL_DEVICE_IMAGE2D_MAX_WIDTH)
	val image3dMaxDepth by info.size_t(CL_DEVICE_IMAGE3D_MAX_DEPTH)
	val image3dMaxHeight by info.size_t(CL_DEVICE_IMAGE3D_MAX_HEIGHT)
	val image3dMaxWidth by info.size_t(CL_DEVICE_IMAGE3D_MAX_WIDTH)
	val imageMaxBufferSize by info.size_t(CL_DEVICE_IMAGE_MAX_BUFFER_SIZE)
	val imageMaxArraySize by info.size_t(CL_DEVICE_IMAGE_MAX_ARRAY_SIZE)
	val linkerAvailable by info.bool(CL_DEVICE_LINKER_AVAILABLE)
	val localMemSize by info.ulong(CL_DEVICE_LOCAL_MEM_SIZE)
	// CL_DEVICE_LOCAL_MEM_SIZE
	val maxClockFrequency by info.uint(CL_DEVICE_MAX_CLOCK_FREQUENCY)
	val maxComputeUnts by info.uint(CL_DEVICE_MAX_COMPUTE_UNITS)
	val maxConstantArgs by info.uint(CL_DEVICE_MAX_CONSTANT_ARGS)
	val maxConstantBufferSize by info.ulong(CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE)
	val maxMemAllocSize by info.ulong(CL_DEVICE_MAX_MEM_ALLOC_SIZE)
	val maxParameterSize by info.size_t(CL_DEVICE_MAX_PARAMETER_SIZE)
	val maxReadImageArgs by info.uint(CL_DEVICE_MAX_READ_IMAGE_ARGS)
	val maxSamplers by info.uint(CL_DEVICE_MAX_SAMPLERS)
	val maxWorkGroupSize by info.size_t(CL_DEVICE_MAX_WORK_GROUP_SIZE)
	val maxWorkItemDimensions by info.uint(CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS)
	// CL_DEVICE_MAX_WORK_ITEM_SIZES
	val maxWriteImageArgs by info.uint(CL_DEVICE_MAX_WRITE_IMAGE_ARGS)
	val memBaseAddrAlign by info.uint(CL_DEVICE_MEM_BASE_ADDR_ALIGN)
	val minDataTypeAlignSize by info.uint(CL_DEVICE_MIN_DATA_TYPE_ALIGN_SIZE)
	val name by info.string(CL_DEVICE_NAME)
	// CL_DEVICE_NATIVE_VECTOR_WIDTH_x
	val openCLCVersion by info.string(CL_DEVICE_OPENCL_C_VERSION)
	val parentDevice by info.ptr(CL_DEVICE_PARENT_DEVICE).then { if (it == NULL) null else CLDevice(it) }
	val partitionMaxSubDevices by info.uint(CL_DEVICE_PARTITION_MAX_SUB_DEVICES)
	// CL_DEVICE_PARTITION_PROPERTIES
	// CL_DEVICE_PARTITION_AFFINITY_DOMAIN
	// CL_DEVICE_PARTITION_TYPE
	val platform by info.ptr(CL_DEVICE_PLATFORM).then(::CLPlatform)
	// CL_DEVICE_PREFERRED_VECTOR_WIDTH_x
	val printfBufferSize by info.size_t(CL_DEVICE_PRINTF_BUFFER_SIZE)
	val preferredInteropUserSync by info.bool(CL_DEVICE_PREFERRED_INTEROP_USER_SYNC)
	val profile by info.string(CL_DEVICE_PROFILE)
	val profilingTimerResolution by info.size_t(CL_DEVICE_PROFILING_TIMER_RESOLUTION)
	// CL_DEVICE_QUEUE_PROPERTIES
	//val referenceCount by info.uint(CL_DEVICE_REFERENCE_COUNT)
	val singleFPConfig by info.ulong(CL_DEVICE_SINGLE_FP_CONFIG).then(::CLDeviceFPConfig)
	val type by info.ptr(CL_DEVICE_TYPE).then { CLDeviceType.get(it) }
	val vendor by info.string(CL_DEVICE_VENDOR)
	val vendorID by info.uint(CL_DEVICE_VENDOR_ID)
	val version by info.string(CL_DEVICE_VERSION)
	val driverVersion by info.string(CL_DRIVER_VERSION)

	override fun toString() = "${super.toString()}: $name ($type)"
}
