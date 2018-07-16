@file:Suppress("UNCHECKED_CAST")

package me.apemanzilla.ktcl

import org.junit.jupiter.api.Assumptions

fun <T> assumeNull(value: T?) = Assumptions.assumeTrue(value == null, "value isn't null")
fun <T> assumeNotNull(value: T?): T = Assumptions.assumeFalse(value == null, "value is null").let { value as T }

private object thing
fun readResource(name: String) = thing::class.java.getResourceAsStream("/$name").reader().readText()
