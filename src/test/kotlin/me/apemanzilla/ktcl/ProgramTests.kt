package me.apemanzilla.ktcl

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

class ProgramTests {
	@TestFactory
	fun `simple sum program`() = getPlatforms().flatMap { it.getDevices() }.map { d ->
		dynamicTest("simple sum program on $d") {
			val ctx = d.createContext()
			val queue = d.createCommandQueue(ctx)
			val program = ctx.createProgram(readResource("sum.cl"))

			program.build()

			val kernel = program.createKernel("sum")

			val a = IntArray(5) { i -> i }      // input A
			val b = IntArray(5) { i -> i * 2 }  // input B
			val c = IntArray(5)                 // output

			val aBuf = ctx.createBuffer(a)
			val bBuf = ctx.createBuffer(b)
			val cBuf = ctx.createBuffer(c, mode = CLBuffer.Flags.Mode.UseHostMemory)

			kernel.setArg(0, aBuf)
			kernel.setArg(1, bBuf)
			kernel.setArg(2, cBuf)

			queue.enqueueNDRangeKernel(kernel, 5)
			queue.finish()

			assertArrayEquals(intArrayOf(0, 3, 6, 9, 12), c)
		}
	}
}
