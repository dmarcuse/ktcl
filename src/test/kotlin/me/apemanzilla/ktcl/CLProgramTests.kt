package me.apemanzilla.ktcl

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

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
			__kernel void copyData(__global const uchar *in, __global uchar *out) {
				int id = get_global_id(0);
				out[id] = in[id];
			}
		""".trimIndent()).buildAsync()

		// create data buffers
		val inBuf = CLBuffer(context, 5)
		val outBuf = CLBuffer(context, 5)

		// create queue
		val queue = context.devices.first()

		// set kernel args
		val kernel = program.get().createKernel("copyData")

		kernel.setArg(0, inBuf)
		kernel.setArg(1, outBuf)
	}
}