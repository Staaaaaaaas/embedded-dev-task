@file:Suppress("WildcardImport")

package com.stas.interactor

import com.stas.debuggerdriver.DELAY_DURATION
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File

internal class CliInteractor : AutoCloseable {
    private var inputStream: BufferedReader? = null
    private var outputStream: BufferedWriter? = null
    private var errorStream: BufferedReader? = null

    private var errorListener: Job? = null

    private var process: Process? = null

    fun start(projectDirectory: String, command: List<String>) {
        process = ProcessBuilder(command).directory(File(projectDirectory)).start()
        inputStream = process?.inputStream?.bufferedReader()
        outputStream = process?.outputStream?.bufferedWriter()
        errorStream = process?.errorStream?.bufferedReader()

        //errorListener = startErrorListener()
    }

    private fun startErrorListener() = CoroutineScope(Dispatchers.IO).launch {
        while (process?.isAlive == true) {
            val line = readErrorLine()
            if (line != null) {
                process?.destroy()
            }
            delay(DELAY_DURATION)
        }
    }

    private fun readLine() = inputStream?.readLine()

    private fun readErrorLine() = errorStream?.readLine()

    fun readUntil(check: (String?) -> Boolean): List<String?> {
        var line: String?
        val res = mutableListOf<String?>()
        do {
            line = readLine()
            res.add(line)
        } while (!check(line))
        return res
    }

    fun write(content: String) = outputStream?.write(content).also { outputStream?.flush() }

    override fun close() {
        inputStream?.close()
        outputStream?.close()
        process?.destroy()
        errorListener?.cancel()
    }
}
