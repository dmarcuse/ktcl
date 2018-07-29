package me.apemanzilla.ktcl.cl10

import me.apemanzilla.ktcl.test
import org.lwjgl.opencl.CL10.*

data class DeviceType internal constructor(internal val mask: Long) {
	val cpu get() = mask.test(CL_DEVICE_TYPE_CPU)
	val gpu get() = mask.test(CL_DEVICE_TYPE_GPU)
	val accelerator get() = mask.test(CL_DEVICE_TYPE_ACCELERATOR)
	val default get() = mask.test(CL_DEVICE_TYPE_DEFAULT)
	val all get() = mask.test(CL_DEVICE_TYPE_ALL)

	private constructor(mask: Int) : this(mask.toLong())

	operator fun plus(other: DeviceType) = DeviceType(mask or other.mask)

	companion object Types {
		val CPU = DeviceType(CL_DEVICE_TYPE_CPU)
		val GPU = DeviceType(CL_DEVICE_TYPE_GPU)
		val Accelerator = DeviceType(CL_DEVICE_TYPE_ACCELERATOR)
		val Default = DeviceType(CL_DEVICE_TYPE_DEFAULT)
		val All = DeviceType(CL_DEVICE_TYPE_ALL)
	}
}
