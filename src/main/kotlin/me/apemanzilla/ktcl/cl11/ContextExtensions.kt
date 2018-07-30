package me.apemanzilla.ktcl.cl11

import me.apemanzilla.ktcl.CLContext
import org.lwjgl.opencl.CL11.CL_CONTEXT_NUM_DEVICES

val CLContext.numDevices get() = info.uint(CL_CONTEXT_NUM_DEVICES)
