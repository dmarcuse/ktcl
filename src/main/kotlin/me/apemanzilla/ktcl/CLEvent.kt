package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL11.*
import org.lwjgl.opencl.CLEventCallbackI
import org.lwjgl.system.MemoryUtil.NULL
import java.util.concurrent.CompletableFuture

/**
 * An OpenCL event, used for synchronization of tasks
 */
class CLEvent internal constructor (handle: Long, retain: Boolean) : CLObject(handle, ::clReleaseEvent) {
	init {
		if (retain) {
			checkCLError(clRetainEvent(handle))
		}
	}

	enum class State(internal val type: Int) {
		QUEUED(CL_QUEUED),
		SUBMITTED(CL_SUBMITTED),
		RUNNING(CL_RUNNING),
		COMPLETE(CL_COMPLETE);

		internal companion object {
			fun get(type: Int) = values().first { it.type == type }
		}
	}

	private companion object {
		fun createEvent(ctx: CLContext): Long {
			val errBuf = BufferUtils.createIntBuffer(1)

			val handle = clCreateUserEvent(ctx.handle, errBuf)
			checkCLError(errBuf[0])
			return handle
		}
	}

	internal constructor(handle: Long) : this(handle, false)

	/**
	 * Creates a new user event
	 */
	constructor(ctx: CLContext) : this(createEvent(ctx))

	private val info = CLInfo(handle, ::clGetEventInfo)

	val queue by info.pointer(CL_EVENT_COMMAND_QUEUE).then(::CLCommandQueue)
	val context by info.pointer(CL_EVENT_CONTEXT).then(::CLContext)
	// CL_EVENT_COMMAND_TYPE
	val executionStatus by info.int(CL_EVENT_COMMAND_EXECUTION_STATUS).then(State.Companion::get)
	val referenceCount by info.uint(CL_EVENT_REFERENCE_COUNT)

	/**
	 * A [CompletableFuture] which completes once this event is completed, returning this event.
	 */
	val future by lazy {
		CompletableFuture<CLEvent>().also { f ->
			if (executionStatus == State.COMPLETE) {
				f.complete(this@CLEvent)
			} else {
				val callback = CLEventCallbackI { evt, status, _ ->
					if (State.get(status) == State.COMPLETE) {
						f.complete(CLEvent(evt, true))
					}
				}

				checkCLError(clSetEventCallback(handle, CL_COMPLETE, callback, NULL))
			}
		}
	}

	/**
	 * Sets the status of a user-created event
	 */
	fun setStatus(state: State) {
		checkCLError(clSetUserEventStatus(handle, state.type))
	}
}