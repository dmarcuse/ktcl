package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.PointerBuffer
import org.lwjgl.opencl.CL10.CL_TRUE
import org.lwjgl.opencl.CLCapabilities
import java.nio.ByteBuffer
import kotlin.reflect.KProperty
import kotlin.properties.ReadOnlyProperty
import java.math.BigInteger
import java.lang.Math.toIntExact

/**
 * Indicates that the necessary OpenCL version is not supported for the operation that was attempted
 */
class CLVersionException internal constructor(message: String, cause: Throwable? = null) : IllegalStateException(message, cause)

/**
 * Provides utilities for retrieving OpenCL info
 */
internal class CLInfo(val infoFunc: (Int, ByteBuffer?, PointerBuffer?) -> Int, val caps: (() -> CLCapabilities)? = null) {
	constructor(id: Long, f: (Long, Int, ByteBuffer?, PointerBuffer?) -> Int, caps: (() -> CLCapabilities)? = null) : this({ i, b, p -> f(id, i, b, p) }, caps)

	companion object {
		/**
		 * Reinterprets this [Int] as an unsigned int (stored in a [Long] to prevent overflows)
		 */
		fun Int.uint() = Integer.toUnsignedLong(this)

		/**
		 * Reinterprets this [Long] as an unsigned long (stored in a [BigInteger] to prevent overflows)
		 */
		fun Long.ulong() = BigInteger(1, BigInteger.valueOf(this).toByteArray())
	}

	/**
	 * A delegate that calls a given function to retrieve a value
	 */
	inner class Delegate<out T>(val param: Int, val getter: (Int) -> T) : ReadOnlyProperty<Any?, T> {
		override operator fun getValue(thisRef: Any?, property: KProperty<*>) = getter(param)

		fun requires(condition: () -> Boolean,
		             lazyMessage: (() -> String) = { "Condition failed" }) = object : ReadOnlyProperty<Any?, T> {
			override fun getValue(thisRef: Any?, property: KProperty<*>): T {
				if (!condition()) {
					throw CLVersionException(lazyMessage())
				}

				return this@Delegate.getValue(thisRef, property)
			}
		}

		fun CL10() = requires({ caps?.invoke()?.OpenCL10 == true }) { "OpenCL 1.0 not supported" }
		fun CL11() = requires({ caps?.invoke()?.OpenCL11 == true }) { "OpenCL 1.1 not supported" }
		fun CL12() = requires({ caps?.invoke()?.OpenCL12 == true }) { "OpenCL 1.2 not supported" }
		fun CL20() = requires({ caps?.invoke()?.OpenCL20 == true }) { "OpenCL 2.0 not supported" }
		fun CL21() = requires({ caps?.invoke()?.OpenCL21 == true }) { "OpenCL 2.1 not supported" }
		fun CL22() = requires({ caps?.invoke()?.OpenCL22 == true }) { "OpenCL 2.2 not supported" }
	}

	/**
	 * Gets the raw data for the given parameter as a [ByteBuffer]
	 */
	fun getRaw(param: Int): ByteBuffer {
		val sizeBuf = BufferUtils.createPointerBuffer(1)
		checkCLError(infoFunc(param, null, sizeBuf))
		sizeBuf.rewind()

		val outBuf = BufferUtils.createByteBuffer(toIntExact(sizeBuf[0]))
		checkCLError(infoFunc(param, outBuf, sizeBuf))
		return outBuf.apply { rewind().limit(toIntExact(sizeBuf[0])) }
	}

	fun getBytes(param: Int) = getRaw(param).toArray()
	fun getInts(param: Int) = getRaw(param).asIntBuffer().toArray()
	fun getUints(param: Int) = getInts(param).map { it.uint() }.toLongArray()
	fun getLongs(param: Int) = getRaw(param).asLongBuffer().toArray()
	fun getUlongs(param: Int) = getLongs(param).map { it.ulong() }.toTypedArray()
	fun getPointers(param: Int) = getRaw(param).let(PointerBuffer::create).toArray()
	fun getBools(param: Int) = getBytes(param).map { it.toInt() == CL_TRUE }.toBooleanArray()
	fun getSizets(param: Int) = getPointers(param)

	fun getByte(param: Int) = getBytes(param).first()
	fun getInt(param: Int) = getInts(param).first()
	fun getUint(param: Int) = getUints(param).first()
	fun getLong(param: Int) = getLongs(param).first()
	fun getUlong(param: Int) = getUlongs(param).first()
	fun getPointer(param: Int) = getPointers(param).first()
	fun getBool(param: Int) = getBools(param).first()
	fun getSizet(param: Int) = getSizets(param).first()
	fun getString(param: Int) = getRaw(param).readString()

	fun bytes(param: Int) = Delegate(param, ::getBytes)
	fun ints(param: Int) = Delegate(param, ::getInts)
	fun uints(param: Int) = Delegate(param, ::getUints)
	fun longs(param: Int) = Delegate(param, ::getLongs)
	fun ulongs(param: Int) = Delegate(param, ::getUlongs)
	fun pointers(param: Int) = Delegate(param, ::getPointers)
	fun bools(param: Int) = Delegate(param, ::getBools)
	fun size_ts(param: Int) = Delegate(param, ::getSizets)

	fun byte(param: Int) = Delegate(param, ::getByte)
	fun int(param: Int) = Delegate(param, ::getInt)
	fun uint(param: Int) = Delegate(param, ::getUint)
	fun long(param: Int) = Delegate(param, ::getLong)
	fun ulong(param: Int) = Delegate(param, ::getUlong)
	fun pointer(param: Int) = Delegate(param, ::getPointer)
	fun bool(param: Int) = Delegate(param, ::getBool)
	fun size_t(param: Int) = Delegate(param, ::getSizet)
	fun string(param: Int) = Delegate(param, ::getString)
}