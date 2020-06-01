package com.nabenik.kobsidian.filereader

import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class SystemFileReader {

    fun createInputStream(filename: String): InputStream{
        val file = File(filename)
        return FileInputStream(file)
    }
}
