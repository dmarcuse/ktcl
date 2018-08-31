package me.apemanzilla.ktcl.test

import me.apemanzilla.ktcl.cl10.createContext
import me.apemanzilla.ktcl.cl10.getDefaultDevice
import me.apemanzilla.ktcl.cl10.getDevices
import me.apemanzilla.ktcl.cl10.getPlatforms
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class BasicTests {
	@TestFactory
	fun `Basic platform tests`() = getPlatforms().map { platform ->
		dynamicContainer("Tests for platform $platform", listOf(
				dynamicTest("Get all devices") { println(platform.getDevices()) },
				dynamicTest("Get default device") { println(platform.getDefaultDevice()) },
				dynamicTest("Create multi-device context") {
					val devices = platform.getDevices()

					Assumptions.assumeFalse(devices.isEmpty())

					println(devices.createContext())
				},
				dynamicTest("Create individual contexts") { println(platform.getDevices().map { it.createContext() }) }
		))
	}
}
