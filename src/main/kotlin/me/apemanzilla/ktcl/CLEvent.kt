package me.apemanzilla.ktcl

import me.apemanzilla.ktcl.CLException.Companion.checkErr
import org.lwjgl.opencl.CL10.*

/**
 * An OpenCL event, representing a task in a [command queue][CLCommandQueue].
 */
class CLEvent internal constructor(handle: Long, retain: Boolean) : CLObject(handle, ::clGetEventInfo) {
	init {
		if (retain) checkErr(clRetainEvent(handle))
	}

	override val releaseFn = ::clReleaseEvent
}
