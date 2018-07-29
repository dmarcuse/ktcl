package me.apemanzilla.ktcl.cl10

import org.lwjgl.opencl.CL10.*

enum class MemCacheType(internal val type: Int) {
	None(CL_NONE),
	ReadOnly(CL_READ_ONLY_CACHE),
	ReadWrite(CL_READ_WRITE_CACHE);

	internal companion object {
		fun get(type: Int) = values().first { it.type == type }
	}
}
