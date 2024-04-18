package com.stas.debuggerdriver

interface DebuggerDriver {
    val pathToDebugger: String
    val projectDirectory: String
    val isLoaded: Boolean
    val isRunning: Boolean

    fun load(projectDirectory: String = ".")
    fun run()
    fun setBreakpoint(fileName: String, lineNumber: Int)
    fun resume()
    fun setBreakpointCallback(callback: () -> Unit)
    fun getBacktrace(): String
}
