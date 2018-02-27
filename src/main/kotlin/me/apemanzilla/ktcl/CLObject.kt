package me.apemanzilla.ktcl

abstract class CLObject internal constructor(val id: Long) {
	override operator fun equals(other: Any?) = other is CLObject && other.id == id
	override fun hashCode() = id.hashCode()
	override fun toString() = "%s [0x%X]".format(javaClass.getSimpleName(), id)
}
