package com.nabenik.kobsidian.client

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2

/**
 * Connects and uploads backup file to dropbox
 */
class DropboxClient(credential: Credential) :
    UploadClient {

    private val client: DbxClientV2

    init {
        val config = DbxRequestConfig.newBuilder("dropbox/kobsidian-backups").build()
        client = DbxClientV2(config, credential.token)
    }

    override fun uploadData():Int{
        return -1
    }


}
