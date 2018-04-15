package me.apemanzilla.ktcl

import org.lwjgl.PointerBuffer
import org.lwjgl.opencl.CL10.CL_FALSE
import org.lwjgl.opencl.CL10.CL_TRUE
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.nio.LongBuffer
import java.nio.charset.Charset
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


inline fun <A, B, C> ReadOnlyProperty<A, B>.then(crossinline transform: (B) -> C) = object : ReadOnlyProperty<A, C> {
	override operator fun getValue(thisRef: A, property: KProperty<*>) = transform(this@then.getValue(thisRef, property))
}

inline fun <T, R> ReadOnlyProperty<T, R>.catch(crossinline alternate: () -> R) = object : ReadOnlyProperty<T, R> {
	override operator fun getValue(thisRef: T, property: KProperty<*>) = try {
		this@catch.getValue(thisRef, property)
	} catch (t: Throwable) {
		alternate()
	}
}

/**
 * Converts this [ByteBuffer] to a [ByteArray], using [ByteBuffer.array] if supported
 */
fun ByteBuffer.toArray() = if (hasArray()) array()!! else ByteArray(rewind().remaining()) { i -> this[i] }

/**
 * Converts this [ByteBuffer] to a [String] using the given [Charset] and stripping null terminator (if present)
 */
fun ByteBuffer.readString(cs: Charset = Charsets.UTF_8) = String(toArray(), cs).replace(Regex("\u0000$"), "")

/**
 * Converts this [IntBuffer] to an [IntArray], using [IntBuffer.array] if supported
 */
fun IntBuffer.toArray() = if (hasArray()) array()!! else IntArray(rewind().remaining()) { i -> this[i] }

/**
 * Converts this [LongBuffer] to a [LongArray], using [LongBuffer.array] if supported
 */
fun LongBuffer.toArray() = if (hasArray()) array()!! else LongArray(rewind().remaining()) { i -> this[i] }

/**
 * Converts this [PointerBuffer] to a [LongArray]
 */
fun PointerBuffer.toArray() = LongArray(rewind().remaining()) { i -> this[i] }

/**
 * Converts [value] to a [Long] and then [puts][PointerBuffer.put] it into the buffer
 */
fun PointerBuffer.put(value: Int) = put(value.toLong())

/**
 * Converts a boolean to [CL_TRUE] or [CL_FALSE] appropriately
 */
internal fun Boolean.toCLBool() = if (this) CL_TRUE else CL_FALSE