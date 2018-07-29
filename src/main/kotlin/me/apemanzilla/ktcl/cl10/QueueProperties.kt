package me.apemanzilla.ktcl.cl10

import me.apemanzilla.ktcl.test
import org.lwjgl.opencl.CL10.CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE
import org.lwjgl.opencl.CL10.CL_QUEUE_PROFILING_ENABLE
import org.lwjgl.system.MemoryUtil.NULL

data class QueueProperties internal constructor(internal val mask: Long) {
	val profiling get() = mask.test(CL_QUEUE_PROFILING_ENABLE)
	val outOfOrderMode get() = mask.test(CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE)

	private constructor(mask: Int) : this(mask.toLong())

	operator fun plus(other: DeviceType) = DeviceType(mask or other.mask)

	companion object Properties {
		val None = QueueProperties(NULL)
		val Profiling = QueueProperties(CL_QUEUE_PROFILING_ENABLE)
		val OutOfOrderExecution = QueueProperties(CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE)
	}
}
