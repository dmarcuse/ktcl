package me.apemanzilla.ktcl

/** A base OpenCL object */
abstract class CLObject internal constructor(val handle: Long, infoFn: InfoFunc) {
	val info = CLInfoWrapper(handle, infoFn)

	protected open val releaseFn: (Long) -> Any? = {}
	protected fun finalize() = releaseFn(handle)

	protected open val descriptor: String by lazy { CLObject::class.java.simpleName }

	override fun toString() = "$descriptor (0x${handle.toString(16)}"
	override fun equals(other: Any?) = (other as? CLObject)?.handle == handle
	override fun hashCode() = handle.hashCode()
}
