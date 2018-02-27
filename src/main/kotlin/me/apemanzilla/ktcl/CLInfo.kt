package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.PointerBuffer
import org.lwjgl.opencl.CL10.CL_TRUE
import java.nio.ByteBuffer
import kotlin.reflect.KProperty
import kotlin.properties.ReadOnlyProperty
import java.math.BigInteger

class CLInfoDelegate<T>(val paramName: Int, val getter: (Int) -> T) : ReadOnlyProperty<Any?, T> {
	override operator fun getValue(thisRef: Any?, property: KProperty<*>) = getter(paramName)
}

class CLInfoWrapper(val infoFunc: (Int, ByteBuffer?, PointerBuffer?) -> Int) {
	constructor(id: Long, getter: (Long, Int, ByteBuffer?, PointerBuffer?) -> Int) : this({ i, b, p -> getter(id, i, b, p) })

	fun getInfoRaw(paramName: Int): ByteBuffer {
		val sizeBuf = BufferUtils.createPointerBuffer(1)
		checkCLError(infoFunc(paramName, null, sizeBuf))

		val outBuf = BufferUtils.createByteBuffer(sizeBuf[0].toInt())
		checkCLError(infoFunc(paramName, outBuf, null))
		return outBuf.rewind()
	}

	fun getInfoString(paramName: Int) = getInfoRaw(paramName).readString()
	fun getInfoInt(paramName: Int) = getInfoRaw(paramName).asIntBuffer()[0]
	fun getInfoUint(paramName: Int) = getInfoInt(paramName).uint()
	fun getInfoLong(paramName: Int) = getInfoRaw(paramName).asLongBuffer()[0]
	fun getInfoUlong(paramName: Int) = getInfoLong(paramName).ulong()
	fun getInfoByte(paramName: Int) = getInfoRaw(paramName)[0]
	fun getInfoBool(paramName: Int) = getInfoInt(paramName) == CL_TRUE
	fun getInfoSizet(paramName: Int) = getInfoRaw(paramName).let(PointerBuffer::create)[0]
	fun getInfoPtr(paramName: Int) = getInfoSizet(paramName)

	fun string(paramName: Int) = CLInfoDelegate(paramName, this::getInfoString)
	fun int(paramName: Int) = CLInfoDelegate(paramName, this::getInfoInt)
	fun uint(paramName: Int) = CLInfoDelegate(paramName, this::getInfoInt).then(Int::uint)
	fun long(paramName: Int) = CLInfoDelegate(paramName, this::getInfoLong)
	fun ulong(paramName: Int) = CLInfoDelegate(paramName, this::getInfoLong).then(Long::ulong)
	fun byte(paramName: Int) = CLInfoDelegate(paramName, this::getInfoByte)
	fun bool(paramName: Int) = CLInfoDelegate(paramName, this::getInfoBool)
	fun size_t(paramName: Int) = CLInfoDelegate(paramName, this::getInfoSizet)
	fun ptr(paramName: Int) = size_t(paramName)
}

fun Int.uint() = Math.toIntExact(Integer.toUnsignedLong(this))
fun Long.ulong() = BigInteger(1, BigInteger.valueOf(this).toByteArray()).longValueExact()
