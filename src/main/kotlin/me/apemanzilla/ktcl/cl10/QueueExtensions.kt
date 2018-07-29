package me.apemanzilla.ktcl.cl10

import me.apemanzilla.ktcl.CLCommandQueue
import me.apemanzilla.ktcl.CLContext
import me.apemanzilla.ktcl.CLDevice
import me.apemanzilla.ktcl.CLException.Companion.checkErr
import me.apemanzilla.ktcl.cl10.QueueProperties.Properties.None
import org.lwjgl.opencl.CL10.*

fun CLDevice.createCommandQueue(ctx: CLContext, properties: QueueProperties = None) = checkErr { e ->
	CLCommandQueue(clCreateCommandQueue(ctx.handle, handle, properties.mask, e), false)
}

fun CLCommandQueue.flush() = checkErr(clFlush(handle))
fun CLCommandQueue.finish() = checkErr(clFinish(handle))
