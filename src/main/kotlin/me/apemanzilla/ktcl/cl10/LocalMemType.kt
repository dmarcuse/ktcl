package me.apemanzilla.ktcl.cl10

import org.lwjgl.opencl.CL10.CL_GLOBAL
import org.lwjgl.opencl.CL10.CL_LOCAL

enum class LocalMemType(internal val type: Int) {
	Local(CL_LOCAL),
	Global(CL_GLOBAL);

	internal companion object {
		fun get(type: Int) = values().first { it.type == type }
	}
}
