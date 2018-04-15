package me.apemanzilla.ktcl

abstract class CLObject internal constructor(internal val handle: Long, private val releaseFn: ((Long) -> Any)? = null) {
	override fun equals(other: Any?) = other is CLObject && other.handle == handle
	override fun hashCode() = handle.hashCode()
	override fun toString() = "%s [0x%x]".format(javaClass.name, handle)

	protected fun finalize() {
		releaseFn?.invoke(handle)
	}
}