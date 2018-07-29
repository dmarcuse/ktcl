package me.apemanzilla.ktcl.cl10

import me.apemanzilla.ktcl.CLCommandQueue
import me.apemanzilla.ktcl.CLEvent
import me.apemanzilla.ktcl.CLException.Companion.checkErr
import me.apemanzilla.ktcl.CLInfoWrapper
import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.*

val CLEvent.queue get() = info.pointer(CL_EVENT_COMMAND_QUEUE).let { CLEvent(it, true) }

/**
 * Block until the given OpenCL event(s) have been executed
 */
fun CLCommandQueue.waitFor(vararg event: CLEvent) {
	when {
		event.isEmpty() -> return
		event.size == 1 -> checkErr(clWaitForEvents(event[0].handle))
		else -> {
			val events = BufferUtils.createPointerBuffer(event.size)
			event.forEach { events.put(it.handle) }
			events.flip()

			checkErr(clWaitForEvents(events))
		}
	}
}

/**
 * Provides access to OpenCL event profiling information
 */
class CLEventProfilingInfo internal constructor(val event: CLEvent) {
	val info = CLInfoWrapper(event.handle, ::clGetEventProfilingInfo)
}

/**
 * Gets event profiling information through an instance of [CLEventProfilingInfo]
 */
fun CLEvent.getProfilingInfo() = CLEventProfilingInfo(this)

val CLEventProfilingInfo.queued get() = info.ulong(CL_PROFILING_COMMAND_QUEUED)
val CLEventProfilingInfo.submit get() = info.ulong(CL_PROFILING_COMMAND_SUBMIT)
val CLEventProfilingInfo.start get() = info.ulong(CL_PROFILING_COMMAND_START)
val CLEventProfilingInfo.end get() = info.ulong(CL_PROFILING_COMMAND_END)
