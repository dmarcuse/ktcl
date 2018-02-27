package me.apemanzilla.ktcl

import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL11.*
import org.lwjgl.opencl.CL12.*

class CLDevice internal constructor(id: Long) : CLObject(id) {
	private val info = CLInfoWrapper(id, ::clGetDeviceInfo)

	val addressBits by info.int(CL_DEVICE_ADDRESS_BITS)
	val available by info.bool(CL_DEVICE_AVAILABLE)
	val builtInKernels by info.string(CL_DEVICE_BUILT_IN_KERNELS).then { it.split(";") }
	val compilerAvailable by info.bool(CL_DEVICE_COMPILER_AVAILABLE)
	// CL_DEVICE_DOUBLE_FP_CONFIG
	val littleEndian by info.bool(CL_DEVICE_ENDIAN_LITTLE)
	val errorCorrectionSupported by info.bool(CL_DEVICE_ERROR_CORRECTION_SUPPORT)
	val nativeKernelsSupported by info.int(CL_DEVICE_EXECUTION_CAPABILITIES).then { it and CL_EXEC_NATIVE_KERNEL != 0 }
	val extensions by info.string(CL_DEVICE_EXTENSIONS).then { it.split(" ") }
	val globalMemCacheSize by info.long(CL_DEVICE_GLOBAL_MEM_CACHE_SIZE)
	val globalMemCacheReadable by info.int(CL_DEVICE_GLOBAL_MEM_CACHE_TYPE).then { it and (CL_READ_ONLY_CACHE or CL_READ_WRITE_CACHE) != 0 }
	val globalMemCacheWritable by info.int(CL_DEVICE_GLOBAL_MEM_CACHE_TYPE).then { it and CL_READ_WRITE_CACHE != 0 }
	val globalMemCachelineSize by info.long(CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE)
	val globalMemSize by info.long(CL_DEVICE_GLOBAL_MEM_SIZE)
	// CL_DEVICE_HALF_FP_CONFIG
	val hostUnifiedMemory by info.bool(CL_DEVICE_HOST_UNIFIED_MEMORY)
	val imageSupport by info.bool(CL_DEVICE_IMAGE_SUPPORT)
	val image2dMaxHeight by info.size_t(CL_DEVICE_IMAGE2D_MAX_HEIGHT)
	val image2dMaxWidth by info.size_t(CL_DEVICE_IMAGE2D_MAX_WIDTH)
	val image3dMaxDepth by info.size_t(CL_DEVICE_IMAGE3D_MAX_DEPTH)
	val image3dMaxHeight by info.size_t(CL_DEVICE_IMAGE3D_MAX_HEIGHT)
	val image3dMaxWidth by info.size_t(CL_DEVICE_IMAGE3D_MAX_WIDTH)
	
}