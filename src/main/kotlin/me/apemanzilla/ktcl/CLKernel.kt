package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL12.CL_KERNEL_ATTRIBUTES
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

/**
 * An OpenCL kernel
 */
class CLKernel : CLObject {
	private companion object {
		fun createKernel(program: CLProgram, name: String): Long {
			val errBuf = BufferUtils.createIntBuffer(1)

			val handle = clCreateKernel(program.handle, name, errBuf)
			checkCLError(errBuf[0])
			return handle
		}
	}

	/**
	 * Creates a kernel from an existing handle, calling [clRetainKernel]. Should not be used for new kernels
	 */
	internal constructor(handle: Long) : super(handle, ::clReleaseKernel) {
		clRetainKernel(handle)
	}

	/**
	 * Creates a kernel with the given name from the given program
	 */
	constructor(program: CLProgram, name: String) : super(createKernel(program, name), ::clReleaseKernel)

	private val info = CLInfo(handle, ::clGetKernelInfo)

	val functionName by info.string(CL_KERNEL_FUNCTION_NAME)
	val numArgs by info.uint(CL_KERNEL_NUM_ARGS)
	val referenceCount by info.uint(CL_KERNEL_REFERENCE_COUNT)
	val context by info.pointer(CL_KERNEL_CONTEXT).then(::CLContext)
	val program by info.pointer(CL_KERNEL_PROGRAM).then(::CLProgram)
	val attributes by info.string(CL_KERNEL_ATTRIBUTES)

	fun setArg(idx: Int, value: Byte) = checkCLError(clSetKernelArg1b(handle, idx, value))
	fun setArg(idx: Int, value: Short) = checkCLError(clSetKernelArg1s(handle, idx, value))
	fun setArg(idx: Int, value: Int) = checkCLError(clSetKernelArg1i(handle, idx, value))
	fun setArg(idx: Int, value: Long) = checkCLError(clSetKernelArg1l(handle, idx, value))
	fun setArg(idx: Int, value: Float) = checkCLError(clSetKernelArg1f(handle, idx, value))
	fun setArg(idx: Int, value: Double) = checkCLError(clSetKernelArg1d(handle, idx, value))

	fun setArg(idx: Int, value: ShortArray) = checkCLError(clSetKernelArg(handle, idx, value))
	fun setArg(idx: Int, value: IntArray) = checkCLError(clSetKernelArg(handle, idx, value))
	fun setArg(idx: Int, value: LongArray) = checkCLError(clSetKernelArg(handle, idx, value))
	fun setArg(idx: Int, value: FloatArray) = checkCLError(clSetKernelArg(handle, idx, value))
	fun setArg(idx: Int, value: DoubleArray) = checkCLError(clSetKernelArg(handle, idx, value))

	fun setArg(idx: Int, value: CLMemory) = checkCLError(clSetKernelArg1p(handle, idx, value.handle))
}
