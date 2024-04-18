package com.stas

import com.stas.debuggerdriver.getLldbDriver

const val LINE_NUMBER = 3

fun main() {
    val driver = getLldbDriver("lldb")
    driver.load("src/main/resources")
    driver.setBreakpoint("main.c", LINE_NUMBER)
    driver.setBreakpointCallback {
        println("Breakpoint hit!")
        println(driver.getBacktrace())
        driver.resume()
    }
    driver.run()
}
