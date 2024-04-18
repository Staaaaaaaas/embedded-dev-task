package com.stas.lldberror

class LldbError(exception: Exception) : Exception("An error occurred while running lldb: ${exception.message}")
