package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.PointerBuffer
import org.lwjgl.opencl.CL10.CL_TRUE
import java.nio.ByteBuffer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun ByteBuffer.toArray(): ByteArray {
	if (hasArray()) {
		return array()
	} else {
		rewind()
		return ByteArray(remaining()).also { get(it) }
	}
}

fun ByteBuffer.readString() = String(toArray(), Charsets.UTF_8).replace(Regex("\u0000$"), "")

inline fun <A, B, C> ReadOnlyProperty<A,B>.then(crossinline transform: (B) -> C) = object : ReadOnlyProperty<A, C> {
	override operator fun getValue(thisRef: A, property: KProperty<*>) = transform(this@then.getValue(thisRef, property))
}
