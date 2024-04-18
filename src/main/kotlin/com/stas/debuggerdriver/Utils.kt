@file:Suppress("TooManyFunctions")

package com.stas.debuggerdriver

import com.stas.interactor.CliInteractor

const val DELAY_DURATION = 100L

val newline: String = System.lineSeparator()

fun String?.isCodeLine() = this?.matches("^(\\s*->)?\\s*\\d+\\s+.*".toRegex()) == true

fun String?.isExitLine() = this?.contains("exited with status") == true

fun String?.isBreakpointLine() = this?.startsWith("Target") == true

fun String?.isSuccessfulSetBreakpointLine() = this?.contains("address") == true

fun String?.isSetBreakpointLine() = this?.startsWith("Breakpoint") == true

fun String?.isLldbStartLine() = this?.startsWith("Current executable set to") == true

fun String?.isResumeLine() = this?.startsWith("Process") == true

fun String?.isLastBacktraceLine() = this?.contains("dyld`start") == true

fun String.compile(compileCommand: String) =
    CliInteractor().use { interactor ->
        interactor.start(this, compileCommand.split(" "))
        interactor.readUntil { it == null }
    }

fun getLldbDriver(pathToDriver: String = "lldb"): DebuggerDriver = LldbDriver(pathToDriver)
