package me.apemanzilla.ktcl

import kotlin.reflect.KProperty
import java.nio.ByteBuffer
import org.lwjgl.PointerBuffer
import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.CL_TRUE

abstract class CLObject internal constructor(val id: Long) {
	override operator fun equals(other: Any?) = other is CLObject && other.id == id
	override fun hashCode() = id.hashCode()
	override fun toString() = "%s [0x%X]".format(javaClass.getSimpleName(), id)
}
