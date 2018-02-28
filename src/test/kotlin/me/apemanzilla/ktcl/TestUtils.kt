package me.apemanzilla.ktcl

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
import org.junit.jupiter.api.DynamicTest

fun standardDevices() = getPlatforms().flatMap { it.getDevices() }

fun anyPlatforms() = getPlatforms().any()
fun anyDevices() = standardDevices().any()

fun <T : Any> KClass<T>.props() = members.mapNotNull { it as? KProperty }.filter { it.visibility == KVisibility.PUBLIC }

fun <T : Any> T.propMap() = this::class.props().map { it.name to it.getter.call(this) }.toMap()

fun <T : Any?> T.eatResult() {}

fun <T : Any> createPropertyTest(t: T) = DynamicTest.dynamicTest("read properties of $t") { t.propMap() }

inline infix fun <A, B, C> ((A) -> B).then(crossinline next: (B) -> C) = { a: A -> next(this@then(a)) }
