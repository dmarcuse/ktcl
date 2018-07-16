package me.apemanzilla.ktcl

import org.lwjgl.PointerBuffer
import java.nio.*

// extension functions to convert primitive buffers to arrays

fun ByteBuffer.toArray() = ByteArray(remaining()) { get() }
fun IntBuffer.toArray() = IntArray(remaining()) { get() }
fun LongBuffer.toArray() = LongArray(remaining()) { get() }
fun PointerBuffer.toArray() = LongArray(remaining()) { get() }
fun FloatBuffer.toArray() = FloatArray(remaining()) { get() }
fun DoubleBuffer.toArray() = DoubleArray(remaining()) { get() }
