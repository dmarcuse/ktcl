package me.apemanzilla.ktcl

import org.lwjgl.PointerBuffer
import java.nio.*

/** Create an array with the contents of this buffer */
fun ByteBuffer.toArray() = ByteArray(rewind().remaining()) { get(it) }

/** Create an array with the contents of this buffer */
fun ShortBuffer.toArray() = ShortArray(rewind().remaining()) { get(it) }

/** Create an array with the contents of this buffer */
fun IntBuffer.toArray() = IntArray(rewind().remaining()) { get(it) }

/** Create an array with the contents of this buffer */
fun LongBuffer.toArray() = LongArray(rewind().remaining()) { get(it) }

/** Create an array with the contents of this buffer */
fun FloatBuffer.toArray() = FloatArray(rewind().remaining()) { get(it) }

/** Create an array with the contents of this buffer */
fun DoubleBuffer.toArray() = DoubleArray(rewind().remaining()) { get(it) }

/** Create an array with the contents of this buffer */
fun PointerBuffer.toArray() = LongArray(rewind().remaining()) { get(it) }

fun ByteBuffer.asPointerBuffer(): PointerBuffer = PointerBuffer.create(this)
