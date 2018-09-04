package me.apemanzilla.ktcl.test

import me.apemanzilla.ktcl.cl10.*
import me.apemanzilla.ktcl.toArray
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.lwjgl.BufferUtils

class SimpleKernelTests {
	@TestFactory
	fun `Simple kernel tests`() = getPlatforms().flatMap { it.getDevices() }.map { d ->
		dynamicTest("Simple kernel execution on $d") {
			val ctx = d.createContext().also { println(it) }
			val cmd = d.createCommandQueue(ctx).also { println(it) }
			val program = ctx.createProgramWithSource(
					"""
						__kernel void sumKernel(__global const int *a, __global const int *b, __global int *c) {
							int id = get_global_id(0);
							c[id] = a[id] + b[id];
						}
					""".trimIndent()
			).also { println(it) }

			program.build()

			val kernel = program.createKernel("sumKernel").also { println(it) }

			val inA = ctx.createBuffer(5 * 4).also { println(it) }
			val inB = ctx.createBuffer(5 * 4).also { println(it) }
			val out = ctx.createBuffer(5 * 4).also { println(it) }

			cmd.enqueueWriteBuffer(to = inA,
					from = BufferUtils.createByteBuffer(5 * 4).apply {
						asIntBuffer().put(intArrayOf(1, 2, 3, 4, 5)).flip()
					}
			)

			cmd.enqueueWriteBuffer(to = inB,
					from = BufferUtils.createByteBuffer(5 * 4).apply {
						asIntBuffer().put(intArrayOf(2, 4, 6, 8, 10)).flip()
					}
			)

			kernel.setArg(0, inA)
			kernel.setArg(1, inB)
			kernel.setArg(2, out)

			cmd.enqueueNDRangeKernel(kernel, 5)

			val outData = BufferUtils.createByteBuffer(5 * 4)
			cmd.enqueueReadBuffer(out, outData)

			assertArrayEquals(intArrayOf(3, 6, 9, 12, 15), outData.asIntBuffer().toArray())
		}
	}
}
