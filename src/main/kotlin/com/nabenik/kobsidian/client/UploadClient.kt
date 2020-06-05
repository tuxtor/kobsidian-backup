package com.nabenik.kobsidian.client

import java.io.InputStream

interface UploadClient{

    fun uploadData(filename:String, stream: InputStream): Int{
        return -1
    }

}
