package me.apemanzilla.ktcl

import org.junit.Assume
import org.junit.BeforeClass
import org.junit.Test

class TestCLPlatform {
	companion object {
		@BeforeClass
		@JvmStatic
		fun checkPlatformsAvailable() = Assume.assumeTrue(getPlatforms().size > 0)
	}

	@Test
	fun testGetDefaultPlatform() {
		println("Default platform: ${getDefaultPlatform()}")
	}

	@Test
	fun testGetAllPlatforms() {
		println("All platforms: ${getPlatforms().joinToString(", ")}")
	}

	@Test
	fun testAllPlatformsInfo() {
		getPlatforms().forEach(CLPlatform::printAllProps)
	}
}