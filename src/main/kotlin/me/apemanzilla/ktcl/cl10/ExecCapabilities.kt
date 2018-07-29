package me.apemanzilla.ktcl.cl10

import me.apemanzilla.ktcl.test
import org.lwjgl.opencl.CL10.CL_EXEC_KERNEL
import org.lwjgl.opencl.CL10.CL_EXEC_NATIVE_KERNEL

data class ExecCapabilities(internal val mask: Long) {
	val kernel get() = mask.test(CL_EXEC_KERNEL)
	val nativeKernel get() = mask.test(CL_EXEC_NATIVE_KERNEL)
}
