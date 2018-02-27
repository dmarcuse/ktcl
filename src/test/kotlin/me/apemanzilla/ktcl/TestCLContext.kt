package me.apemanzilla.ktcl

import org.junit.Assume
import org.junit.Before
import org.junit.Test

class TestCLContext {
	@Before
	fun checkDevicesAvailable() {
		Assume.assumeTrue(getPlatforms().size > 0)
		Assume.assumeTrue(getDefaultPlatform().getDevices().size > 0)
	}

	@Test
	fun createSingleDeviceCtx() {
		println(getDefaultPlatform().getDevices().first().createContext())
	}

	@Test
	fun createAllDeviceCtx() {
		println(getDefaultPlatform().getDevices().createContext())
	}

	@Test
	fun testAllDeviceCtxInfo() {
		getDefaultPlatform().getDevices().createContext().also { ctx ->
			println("All info for $ctx:")
			CLContext::class.props().forEach { println(" -> ${it.name} = ${it.get(ctx)}") }
		}
	}
}