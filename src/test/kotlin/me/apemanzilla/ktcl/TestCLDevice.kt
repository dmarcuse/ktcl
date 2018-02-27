package me.apemanzilla.ktcl

import org.junit.Assume
import org.junit.BeforeClass
import org.junit.Test

class TestCLDevice {
	companion object {
		@BeforeClass
		@JvmStatic
		fun checkPlatformsAvailable() = Assume.assumeTrue(getPlatforms().size > 0)
	}

	@Test
	fun testAllDevicesInfo() {
		getPlatforms().flatMap { it.getDevices() }.forEach(CLDevice::printAllProps)
	}
}