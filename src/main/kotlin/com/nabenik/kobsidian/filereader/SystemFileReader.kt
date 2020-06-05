package com.nabenik.kobsidian.filereader

import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * Creates an InputStream instance from a given file path
 *
 * @author Victor Orozco
 */
class SystemFileReader {

    fun createInputStream(filename: String): InputStream{
        val file = File(filename)
        return FileInputStream(file)
    }
}
