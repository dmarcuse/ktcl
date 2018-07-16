package me.apemanzilla.ktcl

interface BitField {
	val mask: Int
}

infix fun BitField.or(other: Long) = mask.toLong() or other
infix fun BitField.or(other: BitField) = mask.toLong() or other.mask.toLong()
infix fun Long.or(other: BitField) = this or other.mask.toLong()
