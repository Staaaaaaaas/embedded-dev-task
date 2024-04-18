package com.stas.lldberror

class LldbError(exception: Exception) : Exception(exception.message)
