package com.nabenik.kobsidian.client

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import java.io.File
import java.io.InputStream
import java.util.stream.Stream

/**
 * Connects and uploads backup file to dropbox
 */
class DropboxClient(credential: Credential) :
    UploadClient {

    private val client: DbxClientV2

    init {
        val config = DbxRequestConfig.newBuilder("dropbox/kobsidian-backups").build()
        client = DbxClientV2(config, credential.token)

        val account = client.users().currentAccount
        println(account.name.displayName)
    }

    override fun uploadData(filename:String, stream: InputStream):Int{
        return try {
            client.files().uploadBuilder("/${filename}")
                    .uploadAndFinish(stream)
            1;
        }catch (ex:Exception){
            ex.printStackTrace()
            -1;
        }
    }
}
