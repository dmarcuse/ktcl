package me.apemanzilla.ktcl

import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL11.CL_MEM_OFFSET
import org.lwjgl.opencl.CL12.*
import org.lwjgl.opencl.CL20.CL_MEM_USES_SVM_POINTER

/**
 * An OpenCL memory object that can be used to transfer data between a kernel and the host
 */
abstract class CLMemory internal constructor(handle: Long) : CLObject(handle, ::clReleaseMemObject) {
	enum class Flag(private val flag: Int) {
		/** Kernels may read and write to memory object */
		READ_WRITE(CL_MEM_READ_WRITE),

		/** Kernels may only read from memory object */
		READ_ONLY(CL_MEM_READ_ONLY),

		/** Host may only write to memory object */
		HOST_WRITE_ONLY(CL_MEM_HOST_WRITE_ONLY),

		/** Host may only read from memory object */
		HOST_READ_ONLY(CL_MEM_HOST_READ_ONLY),

		/** Host may not access memory object */
		HOST_NO_ACCESS(CL_MEM_HOST_NO_ACCESS);

		internal companion object {
			fun get(flags: Int) = values().map { if ((it.flag and flags) == it.flag) it else null }.filterNotNull()

			fun Iterable<Flag>.toBitfield() = fold(0, { a, b -> a or b.flag })
		}
	}

	private val info = CLInfo(handle, ::clGetMemObjectInfo)

	// CL_MEM_TYPE
	val flags by info.long(CL_MEM_FLAGS).then(Long::toInt).then(Flag.Companion::get)
	val size by info.size_t(CL_MEM_SIZE)
	// CL_MEM_HOST_PTR - don't bother?
	val mapCount by info.uint(CL_MEM_MAP_COUNT)
	val referenceCount by info.uint(CL_MEM_REFERENCE_COUNT)
	val context by info.pointer(CL_MEM_CONTEXT).then(::CLContext)
	// CL_MEM_ASSOCIATED_MEMOBJECT
	val offset by info.size_t(CL_MEM_OFFSET)
	val usesSVMPointer by info.bool(CL_MEM_USES_SVM_POINTER)
}