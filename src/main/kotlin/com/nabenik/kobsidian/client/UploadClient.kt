package com.nabenik.kobsidian.client

import java.io.InputStream

/**
 * Abstract contract for upload clients
 *
 * @author Victor Orozco
 */
interface UploadClient{

    fun uploadData(filename:String, stream: InputStream): Int{
        return -1
    }

}
