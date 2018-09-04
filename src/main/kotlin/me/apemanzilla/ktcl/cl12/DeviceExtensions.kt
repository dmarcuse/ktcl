package me.apemanzilla.ktcl.cl12

import me.apemanzilla.ktcl.CLDevice
import me.apemanzilla.ktcl.cl10.FPConfig
import me.apemanzilla.ktcl.test
import org.lwjgl.opencl.CL12.*

val CLDevice.doubleFPConfig get() = info.long(CL_DEVICE_DOUBLE_FP_CONFIG).let { FPConfig(it) }
val CLDevice.linkerAvailable get() = info.bool(CL_DEVICE_LINKER_AVAILABLE)
val CLDevice.builtInKernels get() = info.ascii(CL_DEVICE_BUILT_IN_KERNELS).split(";")
val CLDevice.imageMaxBufferSize get() = info.size_t(CL_DEVICE_IMAGE_MAX_BUFFER_SIZE)
val CLDevice.imageMaxArraySize get() = info.size_t(CL_DEVICE_IMAGE_MAX_ARRAY_SIZE)
val CLDevice.preferredInteropUserSync get() = info.bool(CL_DEVICE_PREFERRED_INTEROP_USER_SYNC)
val CLDevice.printfBufferSize get() = info.size_t(CL_DEVICE_PRINTF_BUFFER_SIZE)

// TODO: everything w.r.t. device partitioning

val FPConfig.correctlyRoundedDivideSqrt get() = mask.test(CL_FP_CORRECTLY_ROUNDED_DIVIDE_SQRT)
