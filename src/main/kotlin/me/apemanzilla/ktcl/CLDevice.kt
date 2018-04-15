package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL.createDeviceCapabilities
import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL11.*
import org.lwjgl.opencl.CL12.*
import org.lwjgl.opencl.CL20.*
import org.lwjgl.opencl.CL21.*
import org.lwjgl.opencl.KHRImage2DFromBuffer.*
import org.lwjgl.opencl.KHRSPIR.*
import org.lwjgl.system.MemoryUtil.NULL

/**
 * An OpenCL device
 */
class CLDevice internal constructor(handle: Long) : CLObject(handle) {
	class FPConfig internal constructor(bits: Long) {
		private val b = BitField(bits)

		val denormSupported get() = b.test(CL_FP_DENORM)
		val infNanSupported get() = b.test(CL_FP_INF_NAN)
		val roundToNearestSupported get() = b.test(CL_FP_ROUND_TO_NEAREST)
		val roundToZeroSupported get() = b.test(CL_FP_ROUND_TO_ZERO)
		val roundToInfSupported get() = b.test(CL_FP_ROUND_TO_INF)
		val fmaSupported get() = b.test(CL_FP_FMA)
		val softFloat get() = b.test(CL_FP_SOFT_FLOAT)
	}

	class ExecutionCapabilities internal constructor(bits: Long) {
		private val b = BitField(bits)

		val execKernels get() = b.test(CL_EXEC_KERNEL)
		val execNativeKernels get() = b.test(CL_EXEC_NATIVE_KERNEL)
	}

	enum class MemCacheType {
		NONE, READ_ONLY, READ_WRITE;

		internal companion object Getter {
			fun get(type: Long) = when (type.toInt()) {
				CL_NONE -> NONE
				CL_READ_ONLY_CACHE -> READ_ONLY
				CL_READ_WRITE_CACHE -> READ_WRITE
				else -> throw IllegalArgumentException("Unknown cache type $type")
			}
		}
	}

	enum class MemType {
		NONE, LOCAL, GLOBAL;

		internal companion object Getter {
			fun get(type: Long) = when (type.toInt()) {
				CL_NONE -> NONE
				CL_LOCAL -> LOCAL
				CL_GLOBAL -> GLOBAL
				else -> throw IllegalArgumentException("Unknown memory type $type")
			}
		}
	}

	class AffinityDomain internal constructor(bits: Long) {
		val b = BitField(bits)

		val numa get() = b.test(CL_DEVICE_AFFINITY_DOMAIN_NUMA)
		val l4Cache get() = b.test(CL_DEVICE_AFFINITY_DOMAIN_L4_CACHE)
		val l3Cache get() = b.test(CL_DEVICE_AFFINITY_DOMAIN_L3_CACHE)
		val l2Cache get() = b.test(CL_DEVICE_AFFINITY_DOMAIN_L2_CACHE)
		val l1Cache get() = b.test(CL_DEVICE_AFFINITY_DOMAIN_L1_CACHE)
		val nextPartitionable get() = b.test(CL_DEVICE_AFFINITY_DOMAIN_NEXT_PARTITIONABLE)
	}

	class SVMCapabilities internal constructor(bits: Long) {
		val b = BitField(bits)

		val coarseGrainBuffer get() = b.test(CL_DEVICE_SVM_COARSE_GRAIN_BUFFER)
		val fineGrainBuffer get() = b.test(CL_DEVICE_SVM_FINE_GRAIN_BUFFER)
		val fineGrainSystem get() = b.test(CL_DEVICE_SVM_FINE_GRAIN_SYSTEM)
		val atomics get() = b.test(CL_DEVICE_SVM_ATOMICS)
	}

	enum class Type(internal val type: Int) {
		ALL(CL_DEVICE_TYPE_ALL),
		DEFAULT(CL_DEVICE_TYPE_DEFAULT),
		CPU(CL_DEVICE_TYPE_CPU),
		GPU(CL_DEVICE_TYPE_GPU),
		ACCELERATOR(CL_DEVICE_TYPE_ACCELERATOR),
		CUSTOM(CL_DEVICE_TYPE_CUSTOM);

		internal companion object Getter {
			fun get(type: Long) = when (type.toInt()) {
				CL_DEVICE_TYPE_CPU -> CPU
				CL_DEVICE_TYPE_GPU -> GPU
				CL_DEVICE_TYPE_ACCELERATOR -> ACCELERATOR
				CL_DEVICE_TYPE_CUSTOM -> CUSTOM
				else -> throw IllegalStateException("Unknown device type $type")
			}
		}
	}

	internal val caps by lazy { createDeviceCapabilities(handle, platform.caps) }
	private val info = CLInfo(handle, ::clGetDeviceInfo, this::caps)

	val addressBits by info.uint(CL_DEVICE_ADDRESS_BITS).CL10()
	val available by info.bool(CL_DEVICE_AVAILABLE).CL10()
	val builtInKernels by info.string(CL_DEVICE_BUILT_IN_KERNELS).CL12().then { it.split(";") }
	val compilerAvailable by info.bool(CL_DEVICE_AVAILABLE).CL10()
	val doubleFPConfig by info.long(CL_DEVICE_DOUBLE_FP_CONFIG).CL12().then(CLDevice::FPConfig)
	val endianLittle by info.bool(CL_DEVICE_ENDIAN_LITTLE).CL10()
	val errorCorrectionSupport by info.bool(CL_DEVICE_ERROR_CORRECTION_SUPPORT).CL10()
	val executionCapabilities by info.long(CL_DEVICE_EXECUTION_CAPABILITIES).CL10().then(CLDevice::ExecutionCapabilities)
	val extensions by info.string(CL_DEVICE_EXTENSIONS).CL10().then { it.split(" ") }
	val globalMemCacheSize by info.ulong(CL_DEVICE_GLOBAL_MEM_CACHE_SIZE).CL10()
	val globalMemCacheType by info.uint(CL_DEVICE_GLOBAL_MEM_CACHE_TYPE).CL10().then(MemCacheType.Getter::get)
	val globalMemCachelineSize by info.uint(CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE).CL10()
	val globalVariablePreferredTotalSize by info.size_t(CL_DEVICE_GLOBAL_VARIABLE_PREFERRED_TOTAL_SIZE).CL20()
	val ilVersion by info.string(CL_DEVICE_IL_VERSION).CL21()
	val image2dMaxHeight by info.size_t(CL_DEVICE_IMAGE2D_MAX_HEIGHT).CL10()
	val image2dMaxWidth by info.size_t(CL_DEVICE_IMAGE2D_MAX_WIDTH).CL10()
	val image3dMaxDepth by info.size_t(CL_DEVICE_IMAGE3D_MAX_DEPTH).CL10()
	val image3dMaxHeight by info.size_t(CL_DEVICE_IMAGE3D_MAX_HEIGHT).CL10()
	val image3dMaxWidth by info.size_t(CL_DEVICE_IMAGE3D_MAX_WIDTH).CL10()
	val imageBaseAddressAlignment by info.uint(CL_DEVICE_IMAGE_BASE_ADDRESS_ALIGNMENT)
	val imageMaxArraySize by info.size_t(CL_DEVICE_IMAGE_MAX_ARRAY_SIZE).CL12()
	val imageMaxBufferSize by info.size_t(CL_DEVICE_IMAGE_MAX_BUFFER_SIZE).CL12()
	val imagePitchAlignment by info.uint(CL_DEVICE_IMAGE_PITCH_ALIGNMENT)
	val imageSupport by info.bool(CL_DEVICE_IMAGE_SUPPORT).CL10()
	val linkerAvailable by info.bool(CL_DEVICE_LINKER_AVAILABLE).CL12()
	val localMemSize by info.ulong(CL_DEVICE_LOCAL_MEM_SIZE).CL10()
	val localMemType by info.uint(CL_DEVICE_LOCAL_MEM_TYPE).CL10().then(MemType.Getter::get)
	val maxClockFrequency by info.uint(CL_DEVICE_MAX_CLOCK_FREQUENCY).CL10()
	val maxComputeUnits by info.uint(CL_DEVICE_MAX_COMPUTE_UNITS).CL10()
	val maxConstantArgs by info.uint(CL_DEVICE_MAX_CONSTANT_ARGS).CL10()
	val maxConstantBufferSize by info.ulong(CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE).CL10()
	val maxGlobalVariableSize by info.size_t(CL_DEVICE_MAX_GLOBAL_VARIABLE_SIZE).CL20()
	val maxMemAllocSize by info.ulong(CL_DEVICE_MAX_MEM_ALLOC_SIZE).CL10()
	val maxNumSubGroups by info.uint(CL_DEVICE_MAX_NUM_SUB_GROUPS).CL21()
	val maxOnDeviceEvents by info.uint(CL_DEVICE_MAX_ON_DEVICE_EVENTS).CL20()
	val maxOnDeviceQueues by info.uint(CL_DEVICE_MAX_ON_DEVICE_QUEUES).CL20()
	val maxParameterSize by info.size_t(CL_DEVICE_MAX_PARAMETER_SIZE).CL10()
	val maxPipeArgs by info.uint(CL_DEVICE_MAX_PIPE_ARGS).CL20()
	val maxReadImageArgs by info.uint(CL_DEVICE_MAX_READ_IMAGE_ARGS).CL10()
	val maxReadWriteImageArgs by info.uint(CL_DEVICE_MAX_READ_WRITE_IMAGE_ARGS).CL20()
	val maxSamplers by info.uint(CL_DEVICE_MAX_SAMPLERS).CL10()
	val maxWorkGroupSize by info.size_t(CL_DEVICE_MAX_WORK_GROUP_SIZE).CL10()
	val maxWorkItemDimensions by info.uint(CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS).CL10()
	val maxWorkItemSizes by info.size_ts(CL_DEVICE_MAX_WORK_ITEM_SIZES).CL10()
	val maxWriteImageArgs by info.uint(CL_DEVICE_MAX_WRITE_IMAGE_ARGS).CL10()
	val memBaseAddrAlign by info.uint(CL_DEVICE_MEM_BASE_ADDR_ALIGN).CL10()
	val name by info.string(CL_DEVICE_NAME).CL10()
	val nativeVectorWidthChar by info.uint(CL_DEVICE_NATIVE_VECTOR_WIDTH_CHAR).CL11()
	val nativeVectorWidthShort by info.uint(CL_DEVICE_NATIVE_VECTOR_WIDTH_SHORT).CL11()
	val nativeVectorWidthInt by info.uint(CL_DEVICE_NATIVE_VECTOR_WIDTH_INT).CL11()
	val nativeVectorWidthLong by info.uint(CL_DEVICE_NATIVE_VECTOR_WIDTH_LONG).CL11()
	val nativeVectorWidthFloat by info.uint(CL_DEVICE_NATIVE_VECTOR_WIDTH_FLOAT).CL11()
	val nativeVectorWidthDouble by info.uint(CL_DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE).CL11()
	val nativeVectorWidthHalf by info.uint(CL_DEVICE_NATIVE_VECTOR_WIDTH_HALF).CL11()
	val openclCVersion by info.string(CL_DEVICE_OPENCL_C_VERSION).CL11()
	val parentDevice by info.pointer(CL_DEVICE_PARENT_DEVICE).CL12().then(::CLDevice)
	val partitionAffinityDomain by info.long(CL_DEVICE_PARTITION_AFFINITY_DOMAIN).CL12().then(CLDevice::AffinityDomain)
	val partitionMaxSubDevices by info.uint(CL_DEVICE_PARTITION_MAX_SUB_DEVICES).CL12()
	// CL_DEVICE_PARTITION_PROPERTIES
	// CL_DEVICE_PARTITION_TYPE
	val pipeMaxActiveReservations by info.uint(CL_DEVICE_PIPE_MAX_ACTIVE_RESERVATIONS).CL20()
	val pipeMaxPacketSize by info.uint(CL_DEVICE_PIPE_MAX_PACKET_SIZE).CL20()
	val platform: CLPlatform by lazy { CLPlatform(info.getPointer(CL_DEVICE_PLATFORM)) }
	val preferredGlobalAtomicAlignment by info.uint(CL_DEVICE_PREFERRED_GLOBAL_ATOMIC_ALIGNMENT).CL20()
	val preferredInteropUserSync by info.bool(CL_DEVICE_PREFERRED_INTEROP_USER_SYNC).CL12()
	val preferredLocalAtomicAlignment by info.uint(CL_DEVICE_PREFERRED_LOCAL_ATOMIC_ALIGNMENT).CL20()
	val preferredPlatformAtomicAlignment by info.uint(CL_DEVICE_PREFERRED_PLATFORM_ATOMIC_ALIGNMENT).CL20()
	val preferredVectorWidthChar by info.uint(CL_DEVICE_PREFERRED_VECTOR_WIDTH_CHAR).CL10()
	val preferredVectorWidthShort by info.uint(CL_DEVICE_PREFERRED_VECTOR_WIDTH_SHORT).CL10()
	val preferredVectorWidthInt by info.uint(0x1008).CL10() // constant missing in LWJGL
	val preferredVectorWidthLong by info.uint(CL_DEVICE_PREFERRED_VECTOR_WIDTH_LONG).CL10()
	val preferredVectorWidthFloat by info.uint(CL_DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT).CL10()
	val preferredVectorWidthDouble by info.uint(CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE).CL10()
	val preferredVectorWidthHalf by info.uint(CL_DEVICE_PREFERRED_VECTOR_WIDTH_HALF).CL10()
	val printfBufferSize by info.size_t(CL_DEVICE_PRINTF_BUFFER_SIZE).CL12()
	val profile by info.string(CL_DEVICE_PROFILE).CL10()
	val profilingTimerResolution by info.size_t(CL_DEVICE_PROFILING_TIMER_RESOLUTION).CL10()
	val queueOnDevicePreferredSize by info.uint(CL_DEVICE_QUEUE_ON_DEVICE_PREFERRED_SIZE).CL20()
	val queueOnDeviceProperties by info.long(CL_DEVICE_QUEUE_ON_DEVICE_PROPERTIES).CL20().then(CLCommandQueue::Properties)
	val queueOnHostProperties by info.long(CL_DEVICE_QUEUE_ON_HOST_PROPERTIES).CL20().then(CLCommandQueue::Properties)
	val referenceCount by info.uint(CL_DEVICE_REFERENCE_COUNT).CL12()
	val singleFPConfig by info.long(CL_DEVICE_SINGLE_FP_CONFIG).CL10().then(CLDevice::FPConfig)
	val spirVersions by info.string(CL_DEVICE_SPIR_VERSIONS)
	val subgroupIndependentForwardProgress by info.bool(CL_DEVICE_SUB_GROUP_INDEPENDENT_FORWARD_PROGRESS).CL21()
	val svmCapabilities by info.long(CL_DEVICE_SVM_CAPABILITIES).CL20().then(CLDevice::SVMCapabilities)
	// CL_DEVICE_TERMINATE_CAPABILITY_KHR
	val vendor by info.string(CL_DEVICE_VENDOR).CL10()
	val vendorID by info.uint(CL_DEVICE_VENDOR_ID).CL10()
	val version by info.string(CL_DEVICE_VERSION).CL10()
	val driverVersion by info.string(CL_DRIVER_VERSION).CL10()

	override fun toString() = "CLDevice %s [0x%x]".format(name, handle)

	/**
	 * Creates a [context][CLContext] containing only this device
	 *
	 * @param properties An optional set of properties to configure the context
	 */
	fun createContext() = CLContext(this)
}

/**
 * Gets devices from the given platform of the given type.
 *
 * @param platform The platform to query. If `null`, the behavior is implementation-defined.
 * @param type The device type. Defaults to the `ALL` device type, which selects all non-custom devices.
 */
fun getDevices(platform: CLPlatform? = null, type: CLDevice.Type = CLDevice.Type.ALL): List<CLDevice> {
	val platHandle = platform?.handle ?: NULL;
	val sizeBuf = BufferUtils.createIntBuffer(1)
	checkCLError(clGetDeviceIDs(platHandle, type.type.toLong(), null, sizeBuf))
	sizeBuf.rewind()

	val devsBuf = BufferUtils.createPointerBuffer(sizeBuf[0])
	checkCLError(clGetDeviceIDs(platHandle, type.type.toLong(), devsBuf, sizeBuf))
	return List(sizeBuf[0]) { i -> CLDevice(devsBuf[i]) }
}