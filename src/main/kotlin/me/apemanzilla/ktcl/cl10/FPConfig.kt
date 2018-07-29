package me.apemanzilla.ktcl.cl10

import me.apemanzilla.ktcl.test
import org.lwjgl.opencl.CL10.*

data class FPConfig(internal val mask: Long) {
	val denorm get() = mask.test(CL_FP_DENORM)
	val inf get() = mask.test(CL_FP_INF_NAN)
	val nan get() = mask.test(CL_FP_INF_NAN)
	val roundToNearest get() = mask.test(CL_FP_ROUND_TO_NEAREST)
	val roundToZero get() = mask.test(CL_FP_ROUND_TO_ZERO)
	val roundToInf get() = mask.test(CL_FP_ROUND_TO_INF)
	val fma get() = mask.test(CL_FP_FMA)
}
