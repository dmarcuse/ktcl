package me.apemanzilla.ktcl

import me.apemanzilla.ktcl.CLBuffer.Flags
import org.lwjgl.opencl.CL12.*
import java.nio.ByteBuffer

class CLBuffer internal constructor(handle: Long, retain: Boolean) : CLMem(handle, retain) {
	companion object Flags {
		enum class Kernel(override val mask: Int) : BitField {
			ReadWrite(CL_MEM_READ_WRITE),
			ReadOnly(CL_MEM_READ_ONLY),
			WriteOnly(CL_MEM_WRITE_ONLY)
		}

		enum class Host(override val mask: Int) : BitField {
			ReadWrite(0),
			ReadOnly(CL_MEM_HOST_READ_ONLY),
			WriteOnly(CL_MEM_HOST_WRITE_ONLY),
			NoAccess(CL_MEM_HOST_NO_ACCESS)
		}

		enum class Mode(override val mask: Int) : BitField {
			/** If set, host memory will be used by the kernel, negating need for manual copying at the cost of performance */
			UseHostMemory(CL_MEM_USE_HOST_PTR),

			/** If set, host memory will be copied to the buffer */
			CopyHostMemory(CL_MEM_COPY_HOST_PTR)
		}
	}
}

private inline fun <T> ce(f: (IntArray) -> T): T {
	val err = IntArray(1)
	val ret = f(err)
	checkErr(err[0])
	return ret
}

fun CLContext.createBuffer(data: ByteBuffer, kernelAccess: Flags.Kernel = Flags.Kernel.ReadWrite, hostAccess: Flags.Host = Flags.Host.ReadWrite, mode: Flags.Mode = Flags.Mode.CopyHostMemory) = ce { e -> CLBuffer(clCreateBuffer(handle, kernelAccess or hostAccess or mode, data, e), false) }
fun CLContext.createBuffer(data: IntArray, kernelAccess: Flags.Kernel = Flags.Kernel.ReadWrite, hostAccess: Flags.Host = Flags.Host.ReadWrite, mode: Flags.Mode = Flags.Mode.CopyHostMemory) = ce { e -> CLBuffer(clCreateBuffer(handle, kernelAccess or hostAccess or mode, data, e), false) }
