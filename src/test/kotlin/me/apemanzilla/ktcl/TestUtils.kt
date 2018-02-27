package me.apemanzilla.ktcl

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility

fun <T : Any> KClass<T>.props() = members.mapNotNull { it as? KProperty }.filter { it.visibility == KVisibility.PUBLIC }
fun <T> KProperty<T>.get(on: Any?) = getter.call(on)