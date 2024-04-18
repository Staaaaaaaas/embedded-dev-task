package com.stas

import com.stas.debuggerdriver.getLldbDriver
import com.stas.lldberror.LldbError
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.assertThrows
import kotlin.io.path.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.pathString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class DriverTests {
    @Test
    fun shouldThrow() {
        val driver = getLldbDriver()
        assertThrows<IllegalStateException> { driver.run() }
        assertThrows<IllegalStateException> { driver.resume() }
        assertThrows<IllegalStateException> { driver.getBacktrace() }
    }

    @Test
    fun doubleLoad() {
        val driver = getLldbDriver()
        val path = "src/test/resources/test01"
        driver.load(path)
        assertThrows<IllegalStateException> { driver.load(path) }
    }

    @Test
    fun loadRunTest() {
        val toLoad = Path("src/test/resources").listDirectoryEntries().map {
            it.pathString
        }
        toLoad.forEach {
            if(it.endsWith("test03")) return
            val driver = getLldbDriver()
            driver.load(it)
            assertTrue(driver.isLoaded)
            assertFalse(driver.isRunning)
            driver.setBreakpoint("main.c", 3)
            driver.setBreakpointCallback {
                assertTrue(driver.isRunning)
                driver.resume()
            }
            driver.run()
            assertFalse(driver.isRunning)
        }
    }

    @Test
    fun forLoopTest() {
        val driver = getLldbDriver()
        val path = "src/test/resources/test01"
        driver.load(path)
        assertTrue(driver.isLoaded)
        driver.setBreakpoint("main.c", 11)
        var cnt = 0
        driver.setBreakpointCallback {
            assertTrue(driver.isRunning)
            cnt++
            driver.resume()
        }
        driver.run()
        assertEquals(5, cnt)
    }

    @Test
    fun setBreakpointFail() {
        val driver = getLldbDriver()
        val path = "src/test/resources/test01"
        driver.load(path)
        assertThrows<IllegalArgumentException> {
            driver.setBreakpoint(getRandomWord(), -1)
        }
        driver.setBreakpoint("main.c", 1000000)
        assertThrows<LldbError> {
            driver.run()
        }
    }

    @RepeatedTest(10)
    fun badCompilerPath() {
        val driver = getLldbDriver(getRandomWord())
        val path = "src/test/resources/test01"
        driver.load(path)
        assertThrows<LldbError> {
            driver.run()
        }
    }

    @RepeatedTest(10)
    fun noSuchDirectory() {
        val driver = getLldbDriver()
        val path = getRandomWord()
        driver.load(path)
        assertThrows<LldbError> {
            driver.run()
        }
    }

    @RepeatedTest(10)
    fun noExecutable() {
        val driver = getLldbDriver()
        val path = "src/test/resources/test03"
        driver.load(path)
        assertThrows<LldbError> {
            driver.run()
        }
    }
}
