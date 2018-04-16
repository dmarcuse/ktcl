package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.clCreateBuffer
import org.lwjgl.opencl.CL10.clRetainMemObject
import me.apemanzilla.ktcl.CLMemory.Flag.Companion.toBitfield

/**
 * An OpenCL buffer
 */
class CLBuffer : CLMemory {
	private companion object {
		fun createBuf(ctx: CLContext, flags: Iterable<CLMemory.Flag>, size: Long): Long {
			val errBuf = BufferUtils.createIntBuffer(1)

			val handle = clCreateBuffer(ctx.handle, flags.toBitfield().toLong(), size, errBuf)
			checkCLError(errBuf[0])
			return handle
		}
	}

	internal constructor(handle: Long) : super(handle) {
		clRetainMemObject(handle)
	}

	constructor(ctx: CLContext, size: Long, flags: Iterable<Flag>) : super(createBuf(ctx, flags, size))
	constructor(ctx: CLContext, size: Long, vararg flags: Flag) : this(ctx, size, flags.toList())
}