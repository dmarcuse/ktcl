package me.apemanzilla.ktcl.cl11

import me.apemanzilla.ktcl.CLContext
import me.apemanzilla.ktcl.CLEvent
import me.apemanzilla.ktcl.CLException.Companion.checkErr
import org.lwjgl.opencl.CL11.*

val CLEvent.context get() = info.pointer(CL_EVENT_CONTEXT).let { CLContext(it, true) }

fun CLContext.createUserEvent() = checkErr { e -> CLEvent(clCreateUserEvent(handle, e), false) }

fun CLEvent.setUserEventComplete() = checkErr(clSetUserEventStatus(handle, CL_COMPLETE))
