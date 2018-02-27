package me.apemanzilla.ktcl

import org.junit.Assume
import org.junit.Before
import org.junit.Test
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility.PUBLIC

class TestCLDevice {
	@Before
	fun checkPlatformsAvailable() = Assume.assumeTrue(getPlatforms().size > 0)
	
	@Test
	fun testAllDevicesInfo() {
		getPlatforms().flatMap { it.getDevices() }.forEach { device ->
			println("All info for $device:")
			CLDevice::class.members.mapNotNull { it as? KProperty }.filter { it.visibility == PUBLIC }.forEach {
				prop -> println(" -> ${prop.name} = ${prop.getter.call(device)}")
			}
		}
	}
}