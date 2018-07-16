package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.PointerBuffer
import org.lwjgl.opencl.CL10.CL_TRUE
import org.lwjgl.system.MemoryUtil.memASCII
import org.lwjgl.system.MemoryUtil.memASCIISafe
import java.math.BigInteger
import java.nio.ByteBuffer

private fun Int.unsigned() = Integer.toUnsignedLong(this)
private fun Long.unsigned() = BigInteger(1, BigInteger.valueOf(this).toByteArray())

/**
 * A wrapper to make it easier to retrieve OpenCL info
 */
class CLInfoWrapper(val infoFunc: (Int, ByteBuffer?, PointerBuffer?) -> Int) {
	constructor(ptr: Long, f: (Long, Int, ByteBuffer?, PointerBuffer?) -> Int) : this({ i, b, p -> f(ptr, i, b, p) })

	/**
	 * Get the raw bytes for a given parameter
	 */
	fun getRaw(p: Int): ByteBuffer {
		// first, get the data size
		val sizeBuf = BufferUtils.createPointerBuffer(1)
		checkErr(infoFunc(p, null, sizeBuf))

		// then, create a buffer and read it
		val data = BufferUtils.createByteBuffer(Math.toIntExact(sizeBuf[0]))
		checkErr(infoFunc(p, data, null))
		return data.apply { rewind() }
	}

	fun bytes(p: Int) = getRaw(p).toArray()
	fun ints(p: Int) = getRaw(p).asIntBuffer().toArray()
	fun uints(p: Int) = ints(p).map { it.unsigned() }
	fun longs(p: Int) = getRaw(p).asLongBuffer().toArray()
	fun ulongs(p: Int) = longs(p).map { it.unsigned() }
	fun pointers(p: Int) = getRaw(p).let(PointerBuffer::create).toArray()
	fun size_ts(p: Int) = pointers(p)
	fun bools(p: Int) = ints(p).map { it == CL_TRUE }
	fun ascii(p: Int) = memASCIISafe(getRaw(p))?.replace(Regex("\u0000$"), "")

	fun byte(p: Int) = getRaw(p).get()
	fun int(p: Int) = getRaw(p).asIntBuffer().get()
	fun uint(p: Int) = int(p).unsigned()
	fun long(p: Int) = getRaw(p).asLongBuffer().get()
	fun ulong(p: Int) = long(p).unsigned()
	fun pointer(p: Int) = getRaw(p).let(PointerBuffer::create).get()
	fun size_t(p: Int) = pointer(p)
	fun bool(p: Int) = int(p) == CL_TRUE
}
