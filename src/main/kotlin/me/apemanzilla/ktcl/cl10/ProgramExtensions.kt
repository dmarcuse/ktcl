package me.apemanzilla.ktcl.cl10

import me.apemanzilla.ktcl.CLContext
import me.apemanzilla.ktcl.CLDevice
import me.apemanzilla.ktcl.CLException.Companion.checkErr
import me.apemanzilla.ktcl.CLInfoWrapper
import me.apemanzilla.ktcl.CLProgram
import me.apemanzilla.ktcl.CLProgram.BuildInfo
import org.lwjgl.opencl.CL10.*
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

val CLProgram.context get() = info.pointer(CL_PROGRAM_CONTEXT).let { CLContext(it, true) }
val CLProgram.numDevices get() = info.uint(CL_PROGRAM_NUM_DEVICES)
val CLProgram.devices get() = info.pointers(CL_PROGRAM_DEVICES).map { CLDevice(it) }
val CLProgram.source get() = info.ascii(CL_PROGRAM_SOURCE)
val CLProgram.binarySizes get() = info.size_ts(CL_PROGRAM_BINARY_SIZES)

val CLProgram.binaries get(): Map<CLDevice, ByteBuffer> {
	val devs = devices
	val sizes = binarySizes
	val ptrs = info.pointers(CL_PROGRAM_BINARIES)

	return devs.mapIndexed { i, d ->
		d to MemoryUtil.memByteBuffer(ptrs[i], sizes[i].toInt())
	}.toMap()
}

fun CLProgram.getBuildInfo(device: CLDevice) = BuildInfo(CLInfoWrapper { i, b, p -> clGetProgramBuildInfo(handle, device.handle, i, b, p) })

val BuildInfo.status get() = info.int(CL_PROGRAM_BUILD_STATUS).let { BuildStatus.get(it) }
val BuildInfo.options get() = info.ascii(CL_PROGRAM_BUILD_OPTIONS)
val BuildInfo.log get() = info.ascii(CL_PROGRAM_BUILD_LOG)

fun CLContext.createProgramWithSource(vararg sources: String) = checkErr { err ->
	val validSources = sources.filterNot { it.isBlank() }
	require(validSources.isNotEmpty()) { "At least one non-empty source must be passed" }

	CLProgram(clCreateProgramWithSource(handle, validSources.toTypedArray(), err), false)
}

// TODO: clCreateProgramWithBinary
