package me.apemanzilla.ktcl

import me.apemanzilla.ktcl.CLContext
import me.apemanzilla.ktcl.getAllPlatforms
import me.apemanzilla.ktcl.getDevices
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class BasicTests {
	@TestFactory
	fun `test platform and device properties`() = getAllPlatforms().map { p ->
		dynamicContainer("test platform ${p.name}", listOf(
				dynamicContainer("test platform properties", evaluateEachProperty(p)),
				*(p.getDevices().map { d ->
					dynamicContainer("test device ${d.name}", evaluateEachProperty(d))
				}.toTypedArray())
		))
	}

	@Test
	fun `test getDevices without platform`() {
		getDevices()
	}

	@TestFactory
	fun `test create multi-device context`() = getAllPlatforms().map { platform ->
		dynamicTest("create context from all standard devices in $platform") {
			val devices = platform.getDevices()
			Assumptions.assumeTrue(devices.any())
			val ctx = CLContext(platform.getDevices())
		}
	}

	@TestFactory
	fun `test create single-device context`() = getAllPlatforms()
			.flatMap { it.getDevices() }
			.map { d -> dynamicTest("create context from device $d") { d.createContext() } }
}