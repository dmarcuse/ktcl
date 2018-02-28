package me.apemanzilla.ktcl

import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.DynamicContainer.*

class BasicTests {
	@BeforeEach
	fun `check platforms available`() = assumeTrue(anyPlatforms()) { "No OpenCL platforms available" }.eatResult()

	@BeforeEach
	fun `check devices available`() = assumeTrue(anyDevices()) { "No OpenCL devices available" }.eatResult()

	@Test
	fun `test getDefaultPlatform`() = getDefaultPlatform().eatResult()

	@TestFactory
	fun `test CLPlatform properties`() = getPlatforms().map(::createPropertyTest)

	@TestFactory
	fun `test CLDevice properties`() = standardDevices().map(::createPropertyTest)

	@Test
	fun `test createDefaultContext`() = createDefaultContext().eatResult()

	@TestFactory
	fun `test single-device contexts`() = standardDevices().map(CLDevice::createContext).map(::createPropertyTest)

	@TestFactory
	fun `test platform-wide contexts`() = getPlatforms().map(CLPlatform::getDevices).map { it.createContext() }.map(::createPropertyTest)
}