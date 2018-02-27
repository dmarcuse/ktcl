package me.apemanzilla.ktcl

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility

fun <T : Any> KClass<T>.props() = members.mapNotNull { it as? KProperty }.filter { it.visibility == KVisibility.PUBLIC }

fun <T : Any> T.printAllProps() {
	println("All properties for $this:")
	this::class.props().forEach { prop -> println(" -> ${prop.name} = ${prop.getter.call(this)}") }
}