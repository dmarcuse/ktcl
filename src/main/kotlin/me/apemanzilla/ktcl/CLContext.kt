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
			val ctx = clCreateContext(BufferUtils.createPointerBuffer(1).put(0).rewind(), devBuf, null, NULL, errBuf)
			checkCLError(errBuf[0])
			return ctx
		}
	}
	
	internal constructor(device: CLDevice) : this(createContextInternal(listOf(device)))
	internal constructor(devices: Iterable<CLDevice>) : this(createContextInternal(devices))

	private val info = CLInfoWrapper(id, ::clGetContextInfo)

	val referenceCount by info.uint(CL_CONTEXT_REFERENCE_COUNT)
	val numDevices by info.uint(CL_CONTEXT_NUM_DEVICES)
	val devices get() = info.getInfoRaw(CL_CONTEXT_DEVICES).let(PointerBuffer::create).let { buf -> List(buf.remaining()) { i -> CLDevice(buf[i]) } }

	override fun toString() = "${super.toString()} using $devices"
}