package com.stas.debuggerdriver

import com.stas.interactor.CliInteractor
import com.stas.lldberror.LldbError
import java.io.File
import java.io.IOException

class LldbDriver(
    override val pathToDebugger: String,
    private var compileCommand: String = "make",
    private var executable: String = "main"
) : DebuggerDriver {
    private val breakpointLines = mutableListOf<Pair<String, Int>>()

    private var _projectDirectory: String? = null

    private var debugInteractor: CliInteractor? = null

    private var _isRunning = false
    private var paused = false

    private var breakpointCallback: () -> Unit = { resume() }

    override val projectDirectory: String
        get() = _projectDirectory ?: ""

    override fun load(projectDirectory: String) {
        check(!isLoaded) { "Attempted to double load" }
        this._projectDirectory = projectDirectory
    }


    override fun run() = try {
        check(isLoaded) { "Attempted to call `run` before `load`." }
        projectDirectory.compile(compileCommand)

        debugInteractor = CliInteractor()


        debugInteractor.use { interactor ->
            interactor?.start(projectDirectory, listOf(pathToDebugger, executable))
            interactor?.readUntil { it.isLldbStartLine() }
            loadBreakpoints()
            interactor?.write("run$newline")
            _isRunning = true
            debugLoop()
        }
    } catch (exception: IOException) {
        throw LldbError(exception)
    }


    override fun setBreakpoint(fileName: String, lineNumber: Int) {
        check(isLoaded) { "Cannot set breakpoint before loading the driver" }
        require(File(_projectDirectory, fileName).exists()) { "File not found in the project directory" }
        if (!_isRunning) breakpointLines.add(fileName to lineNumber)
        else {
            debugInteractor?.write("br s -f $fileName -l $lineNumber$newline")
            debugInteractor?.readUntil { it.isSetBreakpointLine() }
            val res = debugInteractor?.readUntil { it.isSetBreakpointLine() }
            if (
                res?.any
                { it.isSuccessfulSetBreakpointLine() } == false
            )
                throw LldbError(Exception("Failed setting the breakpoint: ${res.joinToString (newline)}"))
        }
    }

    override fun resume() {
        check(isRunning) { "Cannot call `resume`, the driver is not running" }
        debugInteractor?.write("continue$newline")
        debugInteractor?.readUntil { it.isResumeLine() }
        paused = false
    }

    override fun setBreakpointCallback(callback: () -> Unit) {
        check(isLoaded) { "Attempted to set breakpoint callback before loading the driver" }
        check(!isRunning) { "Attempted to change the breakpoint callback after running the driver" }
        breakpointCallback = callback
    }

    override fun getBacktrace(): String {
        check(isRunning) { "Cannot get backtrace, the driver is not running" }
        check(paused) { "Cannot get backtrace, the driver is not paused" }
        debugInteractor?.write("thread backtrace all$newline")
        val res = debugInteractor?.readUntil { it.isLastBacktraceLine() }
        return res?.drop(1)?.joinToString(newline) ?: ""
    }

    private fun loadBreakpoints() {
        breakpointLines.forEach { breakpoint ->
            debugInteractor?.write("br s -f ${breakpoint.first} -l ${breakpoint.second}$newline")
            val res = debugInteractor?.readUntil { it.isSetBreakpointLine() }
            if (res?.any { it.isSuccessfulSetBreakpointLine() } == false)
                throw LldbError(Exception("Failed setting the breakpoint: ${res.joinToString (newline)}"))
        }
    }

    private fun debugLoop() {
        while (true) {
            val res = debugInteractor?.readUntil {
                !it.isCodeLine() && (it.isBreakpointLine() || it.isExitLine())
            }
            if (
                res?.any { !it.isCodeLine() && it.isExitLine() } == true
            ) {
                debugInteractor?.write("exit$newline")
                _isRunning = false
                break
            }
            paused = true
            breakpointCallback()
        }
    }

    override val isLoaded
        get() = _projectDirectory != null

    override val isRunning
        get() = _isRunning

}
