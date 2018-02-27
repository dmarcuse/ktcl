package me.apemanzilla.ktcl

import org.junit.Assert
import org.junit.Assume
import org.junit.BeforeClass
import org.junit.Test

class TestCLProgram {
	companion object {
		val validProgram = "kernel void myKernel() { }"
		val invalidProgram = "eh"

		lateinit var ctx: CLContext

		@BeforeClass
		@JvmStatic
		fun checkContextAvailable() {
			try {
				ctx = createDefaultContext()
			} catch (e: Exception) {
				Assume.assumeNoException(e)
			}
		}
	}

	@Test
	fun testProgramCreate() {
		println("Created valid program ${ctx.createProgram(listOf(validProgram))}")
	}

	@Test(expected = IllegalArgumentException::class)
	fun testEmptyProgramCreate() {
		ctx.createProgram(listOf())
	}

	@Test(expected = CLProgramBuildException::class)
	fun testInvalidProgramBuild() {
		ctx.createProgram(listOf(invalidProgram)).build()
	}

	@Test
	fun testProgramPreBuildInfo() {
		println("Pre-build info:")
		ctx.createProgram(listOf(validProgram)).printAllProps()
	}

	@Test
	fun testProgramPostBuildInfo() {
		println("Post-build info:")
		ctx.createProgram(listOf(validProgram)).build().printAllProps()
	}

	@Test
	fun testProgramBuildErrorInfo() {
		val program = ctx.createProgram(listOf(invalidProgram))
		
		try {
			program.build()
			Assert.fail("Invalid program was built without exception")
		} catch (_: CLProgramBuildException) {
			println("Invalid program post-build info:")
			program.printAllProps()
		}
	}

	@Test
	fun testProgramBuildErrorLog() {
		val program = ctx.createProgram(listOf(invalidProgram))

		try {
			program.build()
			Assert.fail("Invalid program was built without exception")
		} catch (e: CLProgramBuildException) {
			println("Caught program build exception:")
			println(e.buildLog)
			Assert.assertEquals("Program and exception should have matching build logs", program.buildLog, e.buildLog)
		}
	}
}