package me.apemanzilla.ktcl

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import java.nio.ByteBuffer

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CLProgramTests {
	companion object {
		val context by lazy { getAllPlatforms().first().getDevices().let(::CLContext) }
	}

	@Test
	fun `test compile valid program`() {
		CLProgram(context, "__kernel void myKernel() { }").build()
	}

	@Test
	fun `test compile invalid program`() {
		assertThrows<CLProgramBuildException> {
			CLProgram(context, "__kernel void myKernel() { error }").build()
		}.let(Throwable::printStackTrace)
	}

	@Test
	fun `test run basic program`() {
		// start compiling the program
		val program = CLProgram(context, """
			__kernel void copyData(__global const int *in, __global int *out) {
				int id = get_global_id(0);
				out[id] = in[id];
			}
		""".trimIndent()).buildAsync()

		// create data buffers
		val inBuf = CLBuffer(context, 5)
		val outBuf = CLBuffer(context, 5)

		// create queue
		val queue = CLCommandQueue(context, context.devices.first())

		// setup kernel
		val kernel = program.get().createKernel("copyData")

		kernel.setArg(0, inBuf)
		kernel.setArg(1, outBuf)

		val input = ByteArray(5) { it.toByte() }
		val output = ByteArray(5)
		val outTmp = ByteBuffer.allocateDirect(5)

		// queue up stuff
		val inEvt = queue.enqueueWriteBuffer(inBuf, ByteBuffer.allocateDirect(5).put(input).flip())
		val runEvt = queue.enqueueNDRangeKernel(kernel, 0, 5, inEvt)
		val outEvt = queue.enqueueReadBuffer(outBuf, outTmp, events = *arrayOf(runEvt))

		queue.finish()

		outTmp.rewind().get(output)
		Assertions.assertArrayEquals(input, output)
	}
}