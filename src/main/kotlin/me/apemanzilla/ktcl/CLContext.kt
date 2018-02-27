package me.apemanzilla.ktcl

import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL11.*
import org.lwjgl.opencl.CL12.*
import org.lwjgl.PointerBuffer
import org.lwjgl.BufferUtils
import org.lwjgl.system.MemoryUtil.NULL

class CLContext internal constructor(id: Long) : CLObject(id) {
	private companion object {
		fun createContextInternal(devices: Iterable<CLDevice>): Long {
			val devBuf = BufferUtils.createPointerBuffer(devices.count())
			devices.map(CLDevice::id).forEach { devBuf.put(it) }
			devBuf.rewind()
			
			val errBuf = BufferUtils.createIntBuffer(1)
			val ctx = clCreateContext(BufferUtils.createPointerBuffer(1).put(NULL).rewind(), devBuf, null, NULL, errBuf)
			checkCLError(errBuf[0])
			return ctx
		}
	}
		
	internal constructor(device: CLDevice) : this(createContextInternal(listOf(device)))
	internal constructor(devices: Iterable<CLDevice>) : this(createContextInternal(devices))
	
	fun createCommandQueue(device: CLDevice): CLCommandQueue {
		val errBuf = BufferUtils.createIntBuffer(1)
		val queue = clCreateCommandQueue(id, device.id, NULL, errBuf)
		checkCLError(errBuf[0])
		return CLCommandQueue(queue)
	}
	
	fun createProgram(sources: Collection<String>): CLProgram {
		require(sources.size >= 1) { "Must have at least one source" }
		require(sources.none(String::isEmpty)) { "Sources must not be empty" }
		
		val errBuf = BufferUtils.createIntBuffer(1)
		val program = clCreateProgramWithSource(id, sources.toTypedArray(), errBuf)
		checkCLError(errBuf[0])
		return CLProgram(program)
	}
	
	private val info = CLInfoWrapper(id, ::clGetContextInfo)

	//val referenceCount by info.uint(CL_CONTEXT_REFERENCE_COUNT)
	val numDevices by info.uint(CL_CONTEXT_NUM_DEVICES)
	val devices get() = info.getInfoRaw(CL_CONTEXT_DEVICES).let(PointerBuffer::create).let { buf -> List(buf.remaining()) { i -> CLDevice(buf[i]) } }

	override fun toString() = "${super.toString()} using $devices"
}

fun Iterable<CLDevice>.createContext() = CLContext(this)

fun createDefaultContext(type: CLDeviceType = CLDeviceType.ALL): CLContext {
	val errBuf = BufferUtils.createIntBuffer(1)
	val propsBuf = BufferUtils.createPointerBuffer(3).put(CL_CONTEXT_PLATFORM.toLong()).put(getDefaultPlatform().id).put(NULL).rewind()
	val ctx = clCreateContextFromType(propsBuf, type.mask, null, NULL, errBuf)
	checkCLError(errBuf[0]) { errCode ->
		when (errCode) {
			CL_INVALID_PLATFORM -> RuntimeException("No OpenCL platforms available")
			CL_DEVICE_NOT_AVAILABLE -> RuntimeException("No OpenCL device of type $type available")
			else -> null
		}
	}
	
	return CLContext(ctx)
}
