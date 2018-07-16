package me.apemanzilla.ktcl

import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class ContextTests {
	@Test
	fun `create default context`() {
		val device = assumeNotNull(getDefaultDevice())
		println("Created context ${device.createContext()}")
	}

	@TestFactory
	fun `create individual contexts`() = getPlatforms().flatMap { it.getDevices() }.map { device ->
		dynamicTest("create context for $device") {
			println("Created context ${device.createContext()}")
		}
	}

	@TestFactory
	fun `create group contexts`() = getPlatforms().map { platform ->
		dynamicTest("create context for all devices of $platform") {
			val devices = platform.getDevices()
			assumeTrue(devices.isNotEmpty(), "no devices provided")
			println("Created context ${devices.createContext()}")
		}
	}

	@Test
	fun `test cross-platform context`() {
		val platforms = getPlatforms()
		assumeTrue(platforms.count { it.getDevices().isNotEmpty() } > 1, "<2 platforms with devices")

		assertThrows<IllegalArgumentException> {
			platforms.flatMap { it.getDevices() }.createContext()
		}
	}

	@TestFactory
	fun `create platform context by type`() = getPlatforms().map { platform ->
		dynamicTest("create context by type for platform $platform") {
			assumeNotNull(platform.getDefaultDevice())
			println("Created context ${platform.createContext()}")
		}
	}

	@Test
	fun `create context by type globally`() {
		println("Created default context ${createContext()}")
	}
}
