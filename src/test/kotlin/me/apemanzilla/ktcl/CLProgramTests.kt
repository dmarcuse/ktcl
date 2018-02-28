package me.apemanzilla.ktcl

import me.apemanzilla.ktcl.CLProgramBuildState.BUILD_ERROR
import me.apemanzilla.ktcl.CLProgramBuildState.BUILD_SUCCESS
import me.apemanzilla.ktcl.CLProgramBuildState.NOT_BUILT
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class CLProgramTests {
	companion object {
		val validProgram = "kernel void myKernel() {}"
		val invalidProgram = "kernel myKernel()"

		fun createPrograms(src: String) = standardDevices().map(CLDevice::createContext).map { it.createProgram(listOf(src)) }

		fun validPrograms() = createPrograms(validProgram)
		fun invalidPrograms() = createPrograms(invalidProgram)
	}

	@BeforeEach
	fun `check devices available`() = assumeTrue(anyDevices()) { "No OpenCL devices available" }

	@TestFactory
	fun `test valid programs`() = validPrograms().map {
		dynamicTest("test valid program $it") {
			assertEquals(NOT_BUILT, it.buildState, "invalid pre-build state")
			it.propMap()
			it.build()
			assertEquals(BUILD_SUCCESS, it.buildState, "invalid post-build state")
			it.propMap()
			assertEquals(1, it.numKernels, "invalid kernel count")
			assertTrue(it.kernelNames.contains("myKernel"), "kernel `myKernel` not found")
		}
	}

	@TestFactory
	fun `test invalid programs`() = invalidPrograms().map {
		dynamicTest("test invalid program $it") {
			assertEquals(NOT_BUILT, it.buildState)
			it.propMap()
			val buildFail = assertThrows(CLProgramBuildException::class.java) { it.build() }
			assertEquals(BUILD_ERROR, it.buildState, "invalid post-build-failure state")
			assertEquals(it.buildLog, buildFail.buildLog, "mismatched build logs")
			it.propMap()
		}
	}
}