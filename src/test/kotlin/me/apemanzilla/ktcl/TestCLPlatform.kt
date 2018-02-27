package me.apemanzilla.ktcl

import org.junit.Test
import org.junit.Assume
import org.junit.Before

class TestCLPlatform {
	@Before
	fun checkPlatformsAvailable() = Assume.assumeTrue(getPlatforms().size > 0)
	
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
		getPlatforms().forEach {
			println("Complete info for $it")
			
			listOf(it.name, it.vendor, it.version, it.profile, it.extensions).forEach {
				println(" -> ${it}")
			}
		}
	}
}