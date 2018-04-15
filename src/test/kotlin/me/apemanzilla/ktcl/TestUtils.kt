package me.apemanzilla.ktcl

import me.apemanzilla.ktcl.CLVersionException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.DynamicTest
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility

fun <T : Any> KClass<T>.publicProperties() = members.mapNotNull {it as? KProperty }.filter { it.visibility == KVisibility.PUBLIC}

/**
 * Evaluates every public property of the given object, calling the getter. If a map of expected values is provided,
 * it will be used to assert equality.
 *
 * @param t The object to test
 * @param expected A map of property names to expected values. If the map is `null` or the property name isn't present,
 * no assertions will be made.
 */
fun <T : Any> evaluateEachProperty(t: T, expected: Map<String, Any?>? = null) = t::class.publicProperties().map { p ->
	DynamicTest.dynamicTest("get property ${p.name}") {
		try {
			val got = p.getter.call(t)

			if (expected?.containsKey(p.name) == true) {
				Assertions.assertEquals(expected[p.name], got)
			}
		} catch (e: Exception) {
			Assumptions.assumeFalse(e is CLVersionException || (e is InvocationTargetException && e.cause is CLVersionException)) {
				"Unsupported OpenCL version"
			}

			throw e
		}
	}!!
}