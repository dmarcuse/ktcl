package me.apemanzilla.ktcl

import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL10.*
import org.lwjgl.opencl.CL12.*
import org.lwjgl.opencl.CL20.CL_PROGRAM_BUILD_GLOBAL_VARIABLE_TOTAL_SIZE
import org.lwjgl.opencl.CL21.CL_PROGRAM_IL
import org.lwjgl.opencl.CLProgramCallbackI
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.MemoryUtil.NULL
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

/**
 * An OpenCL program
 */
class CLProgram : CLObject {
	private companion object {
		fun createProgram(ctx: CLContext, sources: Array<String>): Long {
			require(sources.isNotEmpty()) { "Cannot create program without any sources" }
			val errBuf = BufferUtils.createIntBuffer(1)

			val handle = clCreateProgramWithSource(ctx.handle, sources, errBuf)
			checkCLError(errBuf[0])
			return handle
		}

		@Suppress("UNCHECKED_CAST")
		fun createProgram(ctx: CLContext, sources: Iterable<String>) = when (sources) {
			is Array<*> -> createProgram(ctx, sources as Array<String>)
			else -> createProgram(ctx, sources.toList().toTypedArray())
		}
	}

	/**
	 * Creates a program from an existing handle, calling [clRetainProgram].
	 * Should not be used for new programs, only existing ones.
	 */
	internal constructor(handle: Long) : super(handle, ::clReleaseProgram) {
		clRetainProgram(handle)
	}

	/**
	 * Creates a new program from the given source code.
	 */
	constructor(ctx: CLContext, source: String) : super(createProgram(ctx, listOf(source)), ::clReleaseProgram)

	/**
	 * Creates a new program from the given source code set
	 */
	constructor(ctx: CLContext, sources: Iterable<String>) : super(createProgram(ctx, sources), ::clReleaseProgram)

	private val info = CLInfo(handle, ::clGetProgramInfo)

	val referenceCount by info.uint(CL_PROGRAM_REFERENCE_COUNT)
	val context by info.pointer(CL_PROGRAM_CONTEXT).then(::CLContext)
	val numDevices by info.uint(CL_PROGRAM_NUM_DEVICES)
	val devices by info.pointers(CL_PROGRAM_DEVICES).then { it.map(::CLDevice) }
	val source by info.string(CL_PROGRAM_SOURCE)
	val il by info.bytes(CL_PROGRAM_IL)
	val binarySizes by info.size_ts(CL_PROGRAM_BINARY_SIZES)
	val binaries
		get() = {
			val sizes = binarySizes // size of the binary blobs
			val ptrs = info.getPointers(CL_PROGRAM_BINARIES) // pointers to the binaries

			List(sizes.size) { i -> MemoryUtil.memByteBuffer(ptrs[i], sizes[i].toInt()) }
		}
	val numKernels by info.size_t(CL_PROGRAM_NUM_KERNELS)
	val kernelNames by info.string(CL_PROGRAM_KERNEL_NAMES).then { it.split(";") }

	/**
	 * Represents the build status of a [CLProgram] for a specific [CLDevice]
	 */
	enum class BuildStatus(internal val flag: Int) {
		NOT_BUILT(CL_BUILD_NONE),
		IN_PROGRESS(CL_BUILD_IN_PROGRESS),
		SUCCESS(CL_BUILD_SUCCESS),
		ERROR(CL_BUILD_ERROR);

		internal companion object {
			fun get(flag: Int) = values().first { it.flag == flag }
		}
	}

	/**
	 * Device-specific program build info
	 */
	inner class BuildInfo(val device: CLDevice) {
		private val info = CLInfo({ i, b, p -> clGetProgramBuildInfo(handle, device.handle, i, b, p) })

		val status by info.int(CL_PROGRAM_BUILD_STATUS).then(BuildStatus.Companion::get)
		val options by info.string(CL_PROGRAM_BUILD_OPTIONS)
		val log by info.string(CL_PROGRAM_BUILD_LOG)
		// CL_PROGRAM_BINARY_TYPE
		val globalVariableTotalSize by info.size_t(CL_PROGRAM_BUILD_GLOBAL_VARIABLE_TOTAL_SIZE)
	}

	/**
	 * Compiles and links this program asynchronously, returning a [CompletableFuture] which is resolved once the
	 * operation completes. If any devices encounter an error, a [CLProgramBuildException] will be set as an
	 * exceptional result of the [CompletableFuture].
	 *
	 * @param devs set of devices to build program for - defaults to [all devices][devices] associated with this program
	 * @param opts options to pass to the compiler and linker - defaults to an empty string
	 */
	fun buildAsync(devs: Iterable<CLDevice> = devices, opts: String = "") = CompletableFuture<CLProgram>().also { f ->
		val numDevs = devs.count().also { require(it > 0) { "At least one device is required to build" } }

		val devBuf = BufferUtils.createPointerBuffer(numDevs)
		devs.forEach { devBuf.put(it.handle) }
		devBuf.flip()

		val callback = CLProgramCallbackI { h, _ ->
			val program = CLProgram(h)

			program.devices.forEach { d ->
				val buildInfo = program.BuildInfo(d)

				if (buildInfo.status == BuildStatus.ERROR) {
					f.completeExceptionally(CLProgramBuildException(buildInfo.log))

					return@CLProgramCallbackI
				}
			}

			f.complete(program)
		}

		checkCLError(clBuildProgram(handle, devBuf, opts, callback, NULL)) { errCode ->
			when (errCode) {
				CL_BUILD_PROGRAM_FAILURE -> CLProgramBuildException(devices.map(this::BuildInfo).firstOrNull { it.status == BuildStatus.ERROR }?.log)
				else -> null
			}
		}
	}

	/**
	 * Builds this program for a given set of devices, blocking until the operation completes.
	 *
	 * @param devs set of devices to build program for - defaults to [all devices][devices] associated with this program
	 * @param opts options to pass to the compiler and linker - defaults to an empty string
	 *
	 * @throws CLProgramBuildException when any device encounters an error building this program
	 *
	 * @see buildAsync
	 */
	fun build(devs: Iterable<CLDevice> = devices, opts: String = "") = try {
		buildAsync(devs, opts).get()
	} catch (e: ExecutionException) {
		throw (e.cause ?: e)
	}

	/**
	 * Creates a [CLKernel] with the given name from this program
	 */
	fun createKernel(name: String) = CLKernel(this, name)
}