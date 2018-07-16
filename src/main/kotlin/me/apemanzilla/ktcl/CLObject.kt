package me.apemanzilla.ktcl

import org.lwjgl.PointerBuffer
import java.nio.ByteBuffer

/**
 * An OpenCL object
 * @param handle The handle for this object
 */
abstract class CLObject internal constructor(val handle: Long, infoF: (Long, Int, ByteBuffer?, PointerBuffer?) -> Int) {
	/** The info wrapper for this [CLObject] */
	protected val info = CLInfoWrapper(handle, infoF)

	/** The name for this [CLObject], using the class name if not overridden */
	protected open val name: String by lazy { this::class.java.simpleName }

	/** Called from Object.finalize to release the OpenCL handle if necessary */
	protected open val releaseFn: ((Long) -> Any?)? = null

	protected fun finalize() = releaseFn?.invoke(handle)

	override fun hashCode() = handle.hashCode()
	override fun equals(other: Any?) = (other as? CLObject)?.handle == handle
	override fun toString() = "$name (0x${handle.toString(16)})"
}
