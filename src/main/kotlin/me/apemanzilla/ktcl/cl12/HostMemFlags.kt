package me.apemanzilla.ktcl.cl12

import me.apemanzilla.ktcl.cl10.MemFlag
import org.lwjgl.opencl.CL12.*

/** Represents the access level for the host for an OpenCL memory object */
enum class HostAccess(override val mask: Int) : MemFlag {
	ReadOnly(CL_MEM_HOST_READ_ONLY),
	WriteOnly(CL_MEM_HOST_WRITE_ONLY),
	NoAccess(CL_MEM_HOST_NO_ACCESS)
}
