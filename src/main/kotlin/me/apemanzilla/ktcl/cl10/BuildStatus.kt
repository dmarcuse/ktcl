package me.apemanzilla.ktcl.cl10

import org.lwjgl.opencl.CL10.*

enum class BuildStatus(private val mask: Int) {
	None(CL_BUILD_NONE),
	Error(CL_BUILD_ERROR),
	Success(CL_BUILD_SUCCESS),
	InProgress(CL_BUILD_IN_PROGRESS);

	internal companion object {
		fun get(mask: Int) = values().first { it.mask == mask }
	}
}
