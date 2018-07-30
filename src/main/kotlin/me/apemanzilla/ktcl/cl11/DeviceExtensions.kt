package me.apemanzilla.ktcl.cl11

import me.apemanzilla.ktcl.CLDevice
import me.apemanzilla.ktcl.cl10.FPConfig
import me.apemanzilla.ktcl.test
import org.lwjgl.opencl.CL11.*

val CLDevice.preferredVectorWidthHalf get() = info.uint(CL_DEVICE_PREFERRED_VECTOR_WIDTH_HALF)
val CLDevice.hostUnifiedMemory get() = info.bool(CL_DEVICE_HOST_UNIFIED_MEMORY)
val CLDevice.nativeVectorWidthChar get() = info.uint(CL_DEVICE_NATIVE_VECTOR_WIDTH_CHAR)
val CLDevice.nativeVectorWidthShort get() = info.uint(CL_DEVICE_NATIVE_VECTOR_WIDTH_SHORT)
val CLDevice.nativeVectorWidthInt get() = info.uint(CL_DEVICE_NATIVE_VECTOR_WIDTH_INT)
val CLDevice.nativeVectorWidthLong get() = info.uint(CL_DEVICE_NATIVE_VECTOR_WIDTH_LONG)
val CLDevice.nativeVectorWidthFloat get() = info.uint(CL_DEVICE_NATIVE_VECTOR_WIDTH_FLOAT)
val CLDevice.nativeVectorWidthDouble get() = info.uint(CL_DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE)
val CLDevice.nativeVectorWidthHalf get() = info.uint(CL_DEVICE_NATIVE_VECTOR_WIDTH_HALF)
val CLDevice.openclCVersion get() = info.ascii(CL_DEVICE_OPENCL_C_VERSION)

val FPConfig.softFloat get() = mask.test(CL_FP_SOFT_FLOAT)
