package me.apemanzilla.ktcl.test

import me.apemanzilla.ktcl.cl10.*
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.lwjgl.BufferUtils.createByteBuffer
import kotlin.random.Random

class BufferTests {
	companion object {
		const val BUFFER_SIZE = 50
	}

	@TestFactory
	fun `Test buffer copy host_ptr`() = getPlatforms().flatMap { it.getDevices() }.map { dev ->
		dynamicTest("Test buffer copy host_ptr on $dev") {
			val ctx = dev.createContext()
			val queue = dev.createCommandQueue(ctx)

			val data = Random.nextBytes(BUFFER_SIZE)
			val inBuffer = createByteBuffer(BUFFER_SIZE).put(data)

			val clBuffer = ctx.createBuffer(inBuffer, HostMode.CopyHostPtr)

			val outBuffer = createByteBuffer(BUFFER_SIZE)
			queue.enqueueReadBuffer(clBuffer, outBuffer)
			val outData = ByteArray(BUFFER_SIZE) { i -> outBuffer[i] }

			assertArrayEquals(data, outData)
		}
	}
}
