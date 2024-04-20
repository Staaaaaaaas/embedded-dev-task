[![Build](https://github.com/Staaaaaaaas/embedded-dev-task/actions/workflows/EmbeddedDev.yml/badge.svg)](https://github.com/Staaaaaaaas/embedded-dev-task/actions/workflows/EmbeddedDev.yml)
# LLDB debugger

This is a part of my application for Embedded Development Support internship project. The requirement was to implement a toy class for debugging C programs. This implementation uses LLDB debugger.

## Usage

Import the `LldbDriver` class from `com.stas.debuggerdriver`. \
By default the driver will compile the project with `make`, but other options can be specified.

## API

The function `getLldbDriver()` returns an object that implements Driver interface.

```kotlin
val driver = getLldbDriver("/usr/bin/lldb")
driver.load("path/to/project")
driver.setBreakpoint("main.c", LINE_NUMBER)
driver.setBreakpointCallback {
    println(driver.getBacktrace())
    driver.resume()
}
driver.run()
```

## Improvements

This project can be extended to support additional debugger features such as stepping, watches, etc...

## Implementation

The driver communicates with the lldb debugger using `CliInteractor` class. This class and starts new lldb process. It also implements `AutoClosable` interface, to ensure that the process is closed after its usage. In parallel, the interactor checks whether there is output from `errorStream`, if there is - the process is destroyed.
The interaction is hardcoded for MacOS - this should be improved.

The driver throws `LldbError` when encountering an error during interaction with the debugger.

## Why this project

One of my first interactions with programming was with Arduino in my school. 
I really enjoyed playing with it, even though I wasn't able to implement all my ideas back then.
Later I have been toying around with Raspberry Pi, ESP2866, and of course Arduino. I admired the ability to
merge physical word with programming, seeing you robot move is much more rewarding than seeing an output in the console.
This project would allow me to get in touch with this world again, in a professional environment.
