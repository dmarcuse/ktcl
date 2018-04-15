package me.apemanzilla.ktcl

data class BitField(val bits: Long) {
	fun test(other: Long) = (bits and other) == other
	fun test(other: Int) = test(other.toLong())
}