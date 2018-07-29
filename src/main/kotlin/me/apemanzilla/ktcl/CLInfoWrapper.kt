package me.apemanzilla.ktcl

import me.apemanzilla.ktcl.CLException.Companion.checkErr
import org.lwjgl.BufferUtils
import org.lwjgl.PointerBuffer
import org.lwjgl.opencl.CL10.CL_TRUE
import org.lwjgl.system.MemoryUtil.memASCIISafe
import java.math.BigInteger
import java.nio.Buffer
import java.nio.ByteBuffer

internal typealias WrappedInfoFunc = (Int, ByteBuffer?, PointerBuffer?) -> Int
internal typealias InfoFunc = (Long, Int, ByteBuffer?, PointerBuffer?) -> Int

private val Int.unsigned get() = Integer.toUnsignedLong(this)
private val Long.unsigned get() = BigInteger(1, BigInteger.valueOf(this).toByteArray())

/**
 * A convenience class to simplify accessing information for OpenCL objects
 */
class CLInfoWrapper internal constructor(val fn: WrappedInfoFunc) {
	internal constructor(ptr: Long, fn: InfoFunc) : this({ i, b, p -> fn(ptr, i, b, p) })

	/**
	 * Get the raw data for a given parameter as a [ByteBuffer] with dynamic size
	 * @param p The parameter to retrieve data for
	 */
	fun raw(p: Int): ByteBuffer {
		// get the size for the parameter
		val sizeBuf = BufferUtils.createPointerBuffer(1)
		checkErr(fn(p, null, sizeBuf)) { "CLInfoWrapper.raw($p)" }

		// if size is zero, return immediately instead of calling again
		if (sizeBuf[0] == 0L) return BufferUtils.createByteBuffer(0)

		// allocate data buffer and retrieve data
		val dataBuf = BufferUtils.createByteBuffer(sizeBuf[0].toInt())
		checkErr(fn(p, dataBuf, sizeBuf)) { "CLInfoWrapper.raw($p)" }

		// return data
		return dataBuf.apply { rewind().limit(sizeBuf[0].toInt()) }
	}

	/**
	 * Get the raw data for a given parameter as a [ByteBuffer] with a known size
	 * @param p The parameter to retrieve data for
	 * @param size The size of the data
	 */
	fun raw(p: Int, size: Int): ByteBuffer {
		val sizeBuf = BufferUtils.createPointerBuffer(1).put(size.toLong()).flip()
		val dataBuf = BufferUtils.createByteBuffer(size)
		checkErr(fn(p, dataBuf, sizeBuf)) { "CLInfoWrapper.raw($p, $size)" }
		require(sizeBuf[0] >= size) { "Data smaller than required: CLInfoWrapper.raw($p, $size) got ${sizeBuf[0]}" }
		return dataBuf
	}

	fun bytes(p: Int) = raw(p).toArray()
	fun shorts(p: Int) = raw(p).asShortBuffer().toArray()
	fun ints(p: Int) = raw(p).asIntBuffer().toArray()
	fun uints(p: Int) = ints(p).map { it.unsigned }
	fun longs(p: Int) = raw(p).asLongBuffer().toArray()
	fun ulongs(p: Int) = longs(p).map { it.unsigned }
	fun floats(p: Int) = raw(p).asFloatBuffer().toArray()
	fun doubles(p: Int) = raw(p).asDoubleBuffer().toArray()
	fun pointers(p: Int) = raw(p).asPointerBuffer().toArray()
	fun size_ts(p: Int) = pointers(p)
	fun bools(p: Int) = ints(p).map { it == CL_TRUE }

	fun byte(p: Int) = raw(p, 1)[0]
	fun short(p: Int) = raw(p, 2).asShortBuffer()[0]
	fun int(p: Int) = raw(p, 4).asIntBuffer()[0]
	fun uint(p: Int) = int(p).unsigned
	fun long(p: Int) = raw(p, 8).asLongBuffer()[0]
	fun ulong(p: Int) = long(p).unsigned
	fun float(p: Int) = raw(p, 4).asFloatBuffer()[0]
	fun double(p: Int) = raw(p, 8).asDoubleBuffer()[0]
	fun pointer(p: Int) = raw(p).asPointerBuffer()[0]
	fun size_t(p: Int) = pointer(p)
	fun bool(p: Int) = int(p) == CL_TRUE

	fun ascii(p: Int) = raw(p).let(::memASCIISafe)?.replace(Regex("\u0000$"), "") ?: ""
}
