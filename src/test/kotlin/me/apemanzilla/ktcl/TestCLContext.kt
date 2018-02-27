package me.apemanzilla.ktcl

import org.junit.Assume
import org.junit.BeforeClass
import org.junit.Test

class TestCLContext {
	companion object {
		@BeforeClass
		@JvmStatic
		fun checkDevicesAvailable() {
			Assume.assumeTrue(getPlatforms().size > 0)
			Assume.assumeTrue(getDefaultPlatform().getDevices().size > 0)
		}
	}

	@Test
	fun createSingleDeviceCtx() {
		getPlatforms().flatMap { it.getDevices() }.forEach { device ->
			println("Single-device context: ${device.createContext()}")
		}
	}

	@Test
	fun createAllDeviceCtx() {
		getPlatforms().forEach { platform ->
			println("Multi-device context on $platform: ${platform.getDevices().createContext()}")
		}
	}

	@Test
	fun testDefaultCtx() {
		println("Default context: ${createDefaultContext()}")
	}

	@Test
	fun testDefaultCtxInfo() {
		createDefaultContext().printAllProps()
	}
}