# LLDB debugger

This is a part of my application for Embedded Development Support internship project. The requirement was to implement a toy class for debugging C programs. This implementation uses LLDB debugger.

## Usage

Import the `LldbDriver` class from `com.stas.debuggerdriver`. \
By default the driver will compile the project with `make`, but other options can be specified.

## API

```kotlin
val driver = LldbDriver("/usr/bin/lldb")
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

## Why this project
One of my first interactions with programming was with Arduino in my school. 
I really enjoyed playing with it, even though I wasn't able to implement all my ideas back then.
Later I have been toying around with Raspberry Pi, ESP2866, and of course Arduino. I admired the ability to
merge physical word with programming, seeing you robot move is much more rewarding than seeing an output in the console.
This project would allow me to get in touch with this world again, in a professional environment.