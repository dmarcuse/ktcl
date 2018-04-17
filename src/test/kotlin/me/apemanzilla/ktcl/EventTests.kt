package me.apemanzilla.ktcl

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Duration

class EventTests {
	companion object {
		val context by lazy { CLContext(getAllPlatforms().first().getDevices()) }
	}

	@Test
	fun `test user event`() {
		val event = CLEvent(context)

		Thread {
			Thread.sleep(100)
			event.setStatus(CLEvent.State.COMPLETE)
		}.start()

		Assertions.assertTimeoutPreemptively(Duration.ofMillis(200)) {
			event.future.get()
		}
	}
}