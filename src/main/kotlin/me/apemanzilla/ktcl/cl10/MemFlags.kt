package me.apemanzilla.ktcl.cl10

import org.lwjgl.opencl.CL10.*

interface MemFlag {
	val mask: Int

	operator fun plus(other: MemFlag) = object : MemFlag {
		override val mask = this@MemFlag.mask or other.mask
	}
}

/** Represents the access level for kernels for an OpenCL memory object */
enum class KernelAccess(override val mask: Int) : MemFlag {
	ReadOnly(CL_MEM_READ_ONLY),
	ReadWrite(CL_MEM_READ_WRITE),
	WriteOnly(CL_MEM_WRITE_ONLY)
}

/** Optional mode flags that specify how the memory object should interact with the host data */
enum class HostMode(override val mask: Int) : MemFlag {
	/** Data will be read/written to/from the host pointer by the kernel */
	@Deprecated("This flag is inherently unsafe, allowing the OpenCL implementation to retain a pointer to Java memory that may no longer exist.")
	UseHostPtr(CL_MEM_USE_HOST_PTR),

	/** Data will be copied from the host pointer when the memory object is created */
	CopyHostPtr(CL_MEM_COPY_HOST_PTR),

	/** Memory object will be allocated from host-accessible memory */
	AllocHostPtr(CL_MEM_ALLOC_HOST_PTR)
}
