package me.apemanzilla.ktcl

import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL12.*
import org.lwjgl.PointerBuffer
import org.lwjgl.BufferUtils
import org.lwjgl.system.MemoryUtil.NULL

enum class CLProgramBuildState(private val state: Int) {
	NOT_BUILT(CL_BUILD_NONE),
	ERROR(CL_BUILD_ERROR),
	SUCCESS(CL_BUILD_SUCCESS),
	IN_PROGRESS(CL_BUILD_IN_PROGRESS);

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
		require(buildState == CLProgramBuildState.SUCCESS) { "Program not successfully built" }
		
		val errBuf = BufferUtils.createIntBuffer(1)
		val kernel = clCreateKernel(id, name, errBuf)
		checkCLError(errBuf[0])
		
		return CLKernel(kernel)
	}
}