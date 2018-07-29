package me.apemanzilla.ktcl

/** Test a bitfield for a given bit */
fun Long.test(mask: Long) = (this and mask) == mask

/** Test a bitfield for a given bit */
fun Long.test(mask: Int) = (this and mask.toLong()) == mask.toLong()

/** Test a bitfield for a given bit */
fun Int.test(mask: Int) = (this and mask) == mask
