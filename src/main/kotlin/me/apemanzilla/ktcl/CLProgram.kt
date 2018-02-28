package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.PointerBuffer
import org.lwjgl.opencl.CL10.CL_BUILD_ERROR
import org.lwjgl.opencl.CL10.CL_BUILD_IN_PROGRESS
import org.lwjgl.opencl.CL10.CL_BUILD_NONE
import org.lwjgl.opencl.CL10.CL_BUILD_PROGRAM_FAILURE
import org.lwjgl.opencl.CL10.CL_BUILD_SUCCESS
import org.lwjgl.opencl.CL10.CL_PROGRAM_BUILD_LOG
import org.lwjgl.opencl.CL10.CL_PROGRAM_BUILD_OPTIONS
import org.lwjgl.opencl.CL10.CL_PROGRAM_BUILD_STATUS
import org.lwjgl.opencl.CL10.CL_PROGRAM_CONTEXT
import org.lwjgl.opencl.CL10.CL_PROGRAM_DEVICES
import org.lwjgl.opencl.CL10.CL_PROGRAM_NUM_DEVICES
import org.lwjgl.opencl.CL10.CL_PROGRAM_SOURCE
import org.lwjgl.opencl.CL10.clBuildProgram
import org.lwjgl.opencl.CL10.clCreateKernel
import org.lwjgl.opencl.CL10.clGetProgramBuildInfo
import org.lwjgl.opencl.CL10.clGetProgramInfo
import org.lwjgl.opencl.CL12.CL_PROGRAM_KERNEL_NAMES
import org.lwjgl.opencl.CL12.CL_PROGRAM_NUM_KERNELS
import org.lwjgl.system.MemoryUtil.NULL

enum class CLProgramBuildState(private val state: Int) {
	NOT_BUILT(CL_BUILD_NONE),
	BUILD_ERROR(CL_BUILD_ERROR),
	BUILD_SUCCESS(CL_BUILD_SUCCESS),
	BUILD_IN_PROGRESS(CL_BUILD_IN_PROGRESS);

	companion object {
		internal fun get(state: Int) = values().first { it.state == state }
	}
}

class CLProgramBuildException(val buildLog: String) : CLException(CL_BUILD_PROGRAM_FAILURE, buildLog)

class CLProgram internal constructor(id: Long) : CLObject(id) {
	private val info = CLInfoWrapper(id, ::clGetProgramInfo)

	val context by info.ptr(CL_PROGRAM_CONTEXT).then(::CLContext)
	val numDevices by info.uint(CL_PROGRAM_NUM_DEVICES)
	val devices get() = info.getInfoRaw(CL_PROGRAM_DEVICES).let(PointerBuffer::create).let { buf -> List(buf.remaining()) { i -> CLDevice(buf[i]) } }
	val source by info.string(CL_PROGRAM_SOURCE)
	val numKernels by info.size_t(CL_PROGRAM_NUM_KERNELS).catch { 0 }
	val kernelNames by info.string(CL_PROGRAM_KERNEL_NAMES).then { it.split(';') }.catch { listOf() }

	private val buildInfo = CLInfoWrapper { paramName, dataBuf, sizeBuf -> clGetProgramBuildInfo(id, devices.first().id, paramName, dataBuf, sizeBuf) }

	val buildState by buildInfo.int(CL_PROGRAM_BUILD_STATUS).then { CLProgramBuildState.get(it) }
	val buildOptions by buildInfo.string(CL_PROGRAM_BUILD_OPTIONS)
	val buildLog by buildInfo.string(CL_PROGRAM_BUILD_LOG)

	fun build(options: String = ""): CLProgram {
		checkCLError(clBuildProgram(id, null, options, null, NULL)) { errCode ->
			when (errCode) {
				CL_BUILD_PROGRAM_FAILURE -> CLProgramBuildException(buildLog)
				else -> null
			}
		}

		return this
	}

	fun createKernel(name: String): CLKernel {
		require(buildState == CLProgramBuildState.BUILD_SUCCESS) { "Program not successfully built" }

		val errBuf = BufferUtils.createIntBuffer(1)
		val kernel = clCreateKernel(id, name, errBuf)
		checkCLError(errBuf[0])

		return CLKernel(kernel)
	}

	override fun toString() = "${super.toString()} $buildState"
}